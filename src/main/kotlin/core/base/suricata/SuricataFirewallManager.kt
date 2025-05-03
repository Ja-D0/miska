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
        val config = Miska.app.getConfig().ipsConfig

        addressListName = config.addressListName
        mikrotikInInterface = config.mikrotikInInterface
        repeatThreshold = config.repeatThreshold
        repeatRequestCount = config.repeatRequestCount
    }

    suspend fun blockAddress(ipAddress: String, reason: String, timeout: Long?): Boolean {
        if (addAddressToAddressList(ipAddress, reason, timeout)) {
            manageSuricataFilterRules()

            return true
        }

        return false
    }

    @Deprecated("The function is not mandatory for use")
    private suspend fun addressAlreadyExists(ipAddress: String): Boolean {

        val result = requestWithRepeat { repeatCount ->
            var success = true

            info(
                "Checking the availability of address $ipAddress in the address list $addressListName. " +
                        "Attempt ${repeatCount + 1} of $repeatRequestCount.",
            )

            val response =
                MikrotikApiService.getInstance().getAddressListsApi().print(addressListName, ipAddress).execute()

            if (response.isSuccessful && response.body() != null) {
                val addressesLists = response.body()

                success = addressesLists!!.isNotEmpty()

                if (success) {
                    info("Address $ipAddress already exists in the address list $addressListName. Skip.")
                }
            }

            if (response.errorBody() != null) {
                val errorResponse =
                    gsonSerializer.fromJson(response.errorBody().toString(), ErrorResponse::class.java)

                alertUnsuccessfulRequest(errorResponse)
            }

            success
        }

        if (result == null) {
            return true
        }

        return result
    }

    private suspend fun addAddressToAddressList(ipAddress: String, reason: String, timeout: Long?): Boolean {
        val result = requestWithRepeat { repeatCount ->
            var success = false

            info(
                "An attempt to add $ipAddress to Address List $addressListName. " +
                        "Attempt ${repeatCount + 1} of $repeatRequestCount.",
            )

            val response = MikrotikApiService.getInstance().getAddressListsApi()
                .add(
                    AddressListPayload(
                        addressListName,
                        ipAddress,
                        timeout = timeout?.toString(),
                        comment = "Added by Miska. Reason: $reason"
                    )
                )
                .execute()

            if (response.isSuccessful && response.body() != null) {
                info("$ipAddress address are successfully added to address list $addressListName. Reason: $reason")

                success = true
            }

            if (response.errorBody() != null) {
                val errorResponse =
                    gsonSerializer.fromJson(response.errorBody()!!.string(), ErrorResponse::class.java)

                if (addressAlreadyExistsFromErrorResponse(errorResponse)) {
                    info("Address $ipAddress already exists in the address list $addressListName. Skip.")
                } else {
                    alertUnsuccessfulRequest(errorResponse)
                }
            }

            success
        }

        return result ?: false
    }

    private suspend fun addressAlreadyExistsFromErrorResponse(errorResponse: ErrorResponse): Boolean {
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
                    if (rule.disabled) {
                        enableSuricataFilterRule(rule)
                    }
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

            info(
                "An attempt to check the availability of the Suricata filter rule for blocking. " +
                        "Attempt ${repeatCount + 1} of $repeatRequestCount."
            )

            val response: Response<ArrayList<FirewallFilterResponse>> = block()

            if (response.isSuccessful && response.body() != null) {
                val filterRulesList = response.body()

                if (filterRulesList!!.isNotEmpty()) {
                    result = filterRulesList.first()

                    info("Suricata ${result.chain} filter rule was found id = \"${result.id}\"")
                }
            }

            if (response.errorBody() != null) {
                val errorResponse =
                    gsonSerializer.fromJson(response.errorBody().toString(), ErrorResponse::class.java)

                alertUnsuccessfulRequest(errorResponse)
            }

            result
        }

    private suspend fun enableSuricataFilterRule(rule: FirewallFilterResponse): Boolean {
        val result = requestWithRepeat { repeatCount ->
            var success = false

            info(
                "Trying to enable the Suricata filter rule for blocking id = ${rule.id}. " +
                        "Attempt ${repeatCount + 1} of $repeatRequestCount."
            )

            val response = MikrotikApiService.getInstance().getFirewallFilterApi()
                .edit(
                    rule.id.replace("*", ""),
                    FirewallFilterPayload(disabled = false, comment = rule.comment + " Enabled by Miska")
                )
                .execute()

            if (response.isSuccessful && response.body() != null) {
                success = true

                info("The rule of the Suricata id = ${rule.id} filter was turned on for blocking.")
            }

            if (response.errorBody() != null) {
                val errorResponse =
                    gsonSerializer.fromJson(response.errorBody().toString(), ErrorResponse::class.java)

                alertUnsuccessfulRequest(errorResponse)
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

            info(
                "An attempt to create a Suricata filter rule for blocking. " +
                        "Attempt ${repeatCount + 1} of $repeatRequestCount."
            )

            val response = MikrotikApiService.getInstance().getFirewallFilterApi().add(payload).execute()

            if (response.isSuccessful && response.body() != null) {
                val filterRule = response.body()
                success = true

                info("Suricata filter rule for blocking was created id = \"${filterRule!!.id}\"")
            }

            if (response.errorBody() != null) {
                val errorResponse =
                    gsonSerializer.fromJson(response.errorBody().toString(), ErrorResponse::class.java)

                alertUnsuccessfulRequest(errorResponse)
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
                alert("Connection error: " + (connectionException.message ?: "unknown error."))

                if (++repeatCount != repeatRequestCount) {
                    delay(repeatThreshold)
                }
            }
        }

        return null
    }

    private fun alertUnsuccessfulRequest(errorResponse: ErrorResponse) {
        alert(
            """
            Request was unsuccessful.
            - Code: ${errorResponse.error}.
            - Message: ${errorResponse.message}, " +
            - Detail: ${errorResponse.detail}
            """
        )
    }
}