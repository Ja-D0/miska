package com.microtik.core.cli.commands

import com.microtik.core.api.MicrotikApiService
import com.microtik.core.api.endpoints.AddressListsApi
import com.microtik.core.api.exceptions.FailedRequest
import com.microtik.core.api.requestModels.AddressListPut


class AddressListsCommands(
    override val path: String = "address-list",
    override val apiService: AddressListsApi =  MicrotikApiService.getInstance().getAddressListsApi()
): AbstractCommands() {

    fun commandPrint(): String
    {
        var result: String = ""

        val response = apiService.print().execute()

        if (response.isSuccessful && response.body() != null) {
            result = response.body()!!.joinToString("\n") { it.toString() }
        } else {
            throw FailedRequest(response.code(), response.body().toString(), response.message())
        }

        return result
    }

    fun commandAdd(list: String, address: String): String
    {
        var result: String = ""

        val response = apiService.add(AddressListPut(list, address)).execute()

        if (response.isSuccessful && response.body() != null) {
            result = response.body().toString()
        } else {
            throw FailedRequest(response.code(), response.body().toString(), response.message())
        }

        return result
    }

    fun commandRemove(id: String): String
    {
        val response = apiService.remove(id).execute()

        if (!response.isSuccessful) {
            println(response.message())
            throw FailedRequest(response.code(), response.body().toString(), response.message())
        }

        return ""
    }
}