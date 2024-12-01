package com.microtik.core.cli.commands

import com.microtik.core.api.MicrotikApiService
import com.microtik.core.api.endpoints.AddressApi
import com.microtik.core.api.exceptions.FailedRequest
import com.microtik.core.api.requestModels.AddressPut


class AddressCommands : AbstractCommands() {
    override val path: String = "address"
    override val apiService: AddressApi = MicrotikApiService.getInstance().getAddressApi()

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

    fun commandAdd(address: String, interfaces: String): String
    {
        var result: String = ""

        val response = apiService.add(AddressPut(address, interfaces)).execute()

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