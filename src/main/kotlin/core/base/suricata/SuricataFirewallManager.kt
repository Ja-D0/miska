package com.miska.core.base.suricata

import com.google.gson.Gson
import com.miska.Miska
import com.miska.core.api.MikrotikApiService
import com.miska.core.api.requestModels.AddressListPayload
import com.miska.core.api.requestModels.FirewallFilterPayload
import com.miska.core.api.responseModels.ErrorResponse
import com.miska.core.api.responseModels.FirewallFilterResponse
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import retrofit2.Response
import java.net.ConnectException

class SuricataFirewallManager {
    private val addressListName: String
    private val mikrotikInInterface: String
    private val repeatThreshold: Long
    private val repeatRequestCount: Int
    private val gsonSerializer = Gson()

    private val mutex = Mutex()

    init {
        val config = Miska.app.getConfig().suricataIps

        addressListName = config.addressListName
        mikrotikInInterface = config.mikrotikInInterface
        repeatThreshold = config.repeatThreshold
        repeatRequestCount = config.repeatRequestCount
    }

    suspend fun blockAddress(ipAddress: String, reason: String) {
        if (addAddressToAddressList(ipAddress, reason)) {
            manageSuricataFilterRules()
        } else {
            Miska.alert(
                "$ipAddress address was not added to address list $addressListName", "suricata-alert"
            )
        }
    }

    @Deprecated("The function is not mandatory for use")
    suspend fun addressAlreadyExists(ipAddress: String): Boolean {

        val result = requestWithRepeat { repeatCount ->
            var success = true

            Miska.info(
                "Checking the availability of address $ipAddress in the address list $addressListName. " +
                        "Attempt ${repeatCount + 1} of $repeatRequestCount.",
                "suricata-info"
            )

            val response =
                MikrotikApiService.getInstance().getAddressListsApi().print(addressListName, ipAddress).execute()

            if (response.isSuccessful && response.body() != null) {
                val addressesLists = response.body()

                success = addressesLists!!.isNotEmpty()

                if (success) {
                    Miska.info(
                        "Address $ipAddress already exists in the address list $addressListName. Skip.", "suricata-info"
                    )
                }
            }

            if (response.errorBody() != null) {
                val errorResponse =
                    gsonSerializer.fromJson(response.errorBody().toString(), ErrorResponse::class.java)

                Miska.alert( //TODO: добавить категорию лога, иначе в большом потоке не будет понятно к чему эти логи
                    "Request was unsuccessful: code: ${errorResponse.error}, message: ${errorResponse.message}, " +
                            "detail: ${errorResponse.detail}",
                    "suricata-alert"
                )
            }

            success
        }

        if (result == null) {
            return true
        }

        return result
    }

    suspend fun addAddressToAddressList(ipAddress: String, reason: String): Boolean {
        val result = requestWithRepeat { repeatCount ->
            var success = false

            Miska.info(
                "An attempt to add $ipAddress to Address List $addressListName. " +
                        "Attempt ${repeatCount + 1} of $repeatRequestCount.",
                "suricata-info"
            )

            val response = MikrotikApiService.getInstance().getAddressListsApi()
                .add(AddressListPayload(addressListName, ipAddress, comment = "Added by Miska. Reason: $reason"))
                .execute()

            if (response.isSuccessful && response.body() != null) {
                Miska.alert(
                    "$ipAddress address are successfully added to address list $addressListName. Reason: $reason",
                    "suricata-alert"
                )

                success = true
            }

            if (response.errorBody() != null) {
                val errorResponse =
                    gsonSerializer.fromJson(response.errorBody()!!.string(), ErrorResponse::class.java)

                if (addressAlreadyExistsFromErrorResponse(errorResponse)) {
                    Miska.info(
                        "Address $ipAddress already exists in the address list $addressListName. Skip.", "suricata-info"
                    )

                    success = true
                } else {
                    Miska.alert( //TODO: добавить категорию лога, иначе в большом потоке не будет понятно к чему эти логи
                        "Request was unsuccessful: code: ${errorResponse.error}, message: ${errorResponse.message}, " +
                                "detail: ${errorResponse.detail}",
                        "suricata-alert"
                    )
                }
            }

            success
        }

        return result ?: false
    }

    suspend fun addressAlreadyExistsFromErrorResponse(errorResponse: ErrorResponse): Boolean {
        val expectedMessage = "Bad Request"
        val expectedStatus = 400
        val expectedDetail = "failure: already have such entry"

        return errorResponse.run { error == expectedStatus && message == expectedMessage && detail == expectedDetail }
    }

    /**
     * Выполняет проверку правила фильтра для работы IPS.
     * - Если правило существует, то оно будет проверено на статус активности. Если правило не активно, оно будет включено.
     * - Если правило не существует, оно будет создано и включено по умолчанию.
     *
     * @return [Unit]
     */
    private suspend fun manageSuricataFilterRules() {
        mutex.withLock {
            val needRules = arrayListOf(
                FirewallFilterPayload(
                    "drop",
                    "input",
                    srcAddressList = addressListName,
                    inInterface = mikrotikInInterface,
                    log = true,
                    logPrefix = "suricata-input-rule",
                    comment = "Created by Miska."
                ),
                FirewallFilterPayload(
                    "drop",
                    "forward",
                    srcAddressList = addressListName,
                    log = true,
                    logPrefix = "suricata-forward-src-rule",
                    comment = "Created by Miska."
                ),
                FirewallFilterPayload(
                    "drop",
                    "forward",
                    dstAddressList = addressListName,
                    log = true,
                    logPrefix = "suricata-forward-dst-rule",
                    comment = "Created by Miska."
                )
            )

            for (needRule in needRules) {
                val rule: FirewallFilterResponse? = checkSuricataRuleExists {
                    MikrotikApiService.getInstance().getFirewallFilterApi()
                        .print(
                            needRule.chain,
                            needRule.srcAddressList,
                            needRule.dstAddressList,
                            inInterface = needRule.inInterface,
                            action = needRule.action
                        ).execute()
                }

                if (rule != null) {
                    enableSuricataFilterRule(rule)
                } else {
                    createSuricataFilterRule(needRule)
                }
            }

            true
        }
    }

    private suspend fun checkSuricataRuleExists(block: suspend () -> Response<ArrayList<FirewallFilterResponse>>): FirewallFilterResponse? =
        requestWithRepeat { repeatCount ->
            var result: FirewallFilterResponse? = null

            Miska.info(
                "An attempt to check the availability of the Suricata filter rule for blocking. " +
                        "Attempt ${repeatCount + 1} of $repeatRequestCount.",
                "suricata-info"
            )

            val response: Response<ArrayList<FirewallFilterResponse>> = block()

            if (response.isSuccessful && response.body() != null) {
                val filterRulesList = response.body()

                if (filterRulesList!!.isNotEmpty()) {
                    result = filterRulesList.first()

                    Miska.info("Suricata ${result.chain} filter rule was found id = \"${result.id}\"", "suricata-info")
                }
            }

            if (response.errorBody() != null) {
                val errorResponse =
                    gsonSerializer.fromJson(response.errorBody().toString(), ErrorResponse::class.java)

                Miska.alert(
                    "Request was unsuccessful: code: ${errorResponse.error}, message: ${errorResponse.message}, " +
                            "detail: ${errorResponse.detail}",
                    "suricata-alert"
                )
            }

            result
        }

    private suspend fun enableSuricataFilterRule(rule: FirewallFilterResponse): Boolean {
        val result = requestWithRepeat { repeatCount ->
            var success = false

            Miska.info(
                "Trying to enable the Suricata filter rule for blocking id = ${rule.id}. " +
                        "Attempt ${repeatCount + 1} of $repeatRequestCount.",
                "suricata-info"
            )

            val response = MikrotikApiService.getInstance().getFirewallFilterApi()
                .edit(
                    rule.id.replace("*", ""),
                    FirewallFilterPayload(disabled = false, comment = rule.comment + " Enabled by Miska")
                )
                .execute()

            //TODO: необходимо обработать IOException при неверном адресе сервера

            if (response.isSuccessful && response.body() != null) {
                success = true

                Miska.info(
                    "The rule of the Suricata id = ${rule.id} filter was turned on for blocking.",
                    "suricata-info"
                )
            }

            if (response.errorBody() != null) {
                val errorResponse =
                    gsonSerializer.fromJson(response.errorBody().toString(), ErrorResponse::class.java)

                Miska.alert( //TODO: добавить категорию лога, иначе в большом потоке не будет понятно к чему эти логи
                    "Request was unsuccessful: code: ${errorResponse.error}, message: ${errorResponse.message}, " +
                            "detail: ${errorResponse.detail}",
                    "suricata-alert"
                )
            }

            success
        }

        if (result == null) {
            return false
        }

        return result
    }

    private suspend fun createSuricataFilterRule(payload: FirewallFilterPayload): Boolean {
        val result = requestWithRepeat { repeatCount ->
            var success = false

            Miska.info(
                "An attempt to create a Suricata filter rule for blocking. " +
                        "Attempt ${repeatCount + 1} of $repeatRequestCount.",
                "suricata-info"
            )

            val response = MikrotikApiService.getInstance().getFirewallFilterApi().add(payload).execute()

            if (response.isSuccessful && response.body() != null) {
                val filterRule = response.body()
                success = true
                Miska.info(
                    "Suricata filter rule for blocking was created id = \"${filterRule!!.id}\"",
                    "suricata-info"
                )
            }

            if (response.errorBody() != null) {
                val errorResponse =
                    gsonSerializer.fromJson(response.errorBody().toString(), ErrorResponse::class.java)

                Miska.alert( //TODO: добавить категорию лога, иначе в большом потоке не будет понятно к чему эти логи
                    "Request was unsuccessful: code: ${errorResponse.error}, message: ${errorResponse.message}, " +
                            "detail: ${errorResponse.detail}",
                    "suricata-alert"
                )
            }

            success
        }

        if (result == null) {
            return false
        }

        return result
    }

    private suspend inline fun <T> requestWithRepeat(request: (Int) -> T): T? {
        var repeatCount = 0

        while (repeatCount < repeatRequestCount) {
            try {
                return request(repeatCount)
            } catch (connectionException: ConnectException) {
                Miska.alert("Connection error: " + (connectionException.message ?: "unknown error."), "suricata-alert")

                if (++repeatCount != repeatRequestCount) {
                    delay(repeatThreshold)
                }
            }
        }

        return null
    }
}