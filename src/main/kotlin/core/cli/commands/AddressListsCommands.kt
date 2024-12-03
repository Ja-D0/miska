package com.microtik.core.cli.commands

import com.microtik.core.api.MicrotikApiService
import com.microtik.core.api.endpoints.AddressListsApi
import com.microtik.core.api.exceptions.FailedRequest
import com.microtik.core.api.requestModels.AddressListPut
import com.microtik.core.cli.annotations.Command
import com.microtik.core.cli.annotations.CommandType
import com.microtik.core.cli.annotations.Option

class AddressListsCommands: AbstractCommands() {
    override val path: String = "address-list"
    override val apiService: AddressListsApi =  MicrotikApiService.getInstance().getAddressListsApi()

    @Command("print", CommandType.COMMAND, "Выводит список листов")
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

    @Command("add", CommandType.COMMAND, "Добавляет в лист адрес")
    fun commandAdd(
        @Option("l", "list", true, "Название листа")
        list: String,
        @Option("a", "address", true, "IP адрес")
        address: String
    ): String
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

    @Command("remove", CommandType.COMMAND, "Удаляет адрес из листа")
    fun commandRemove(
        @Option("i", "id", true, "Номер записи")
        id: String
    ): String
    {
        val response = apiService.remove(id).execute()

        if (!response.isSuccessful) {
            println(response.message())
            throw FailedRequest(response.code(), response.body().toString(), response.message())
        }

        return ""
    }
}