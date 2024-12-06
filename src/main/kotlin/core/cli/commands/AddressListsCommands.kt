package com.microtik.core.cli.commands

import com.microtik.core.api.MicrotikApiService
import com.microtik.core.api.endpoints.AddressListsApi
import com.microtik.core.api.requestModels.AddressListPayload
import com.microtik.core.api.responseModels.AddressListsResponse
import com.microtik.core.cli.annotations.Command
import com.microtik.core.cli.annotations.CommandType
import com.microtik.core.cli.annotations.Option

class AddressListsCommands : AbstractCommands() {
    override val path: String = "address-list"
    override val apiService: AddressListsApi = MicrotikApiService.getInstance().getAddressListsApi()

    @Command("print", CommandType.COMMAND, "Показать элементы")
    fun commandPrint(): String = runRequest<ArrayList<AddressListsResponse>> { apiService.print().execute() }

    @Command("add", CommandType.COMMAND, "Добавляет в лист адрес")
    fun commandAdd(
        @Option("l", "list", true, "Название листа")
        list: String,
        @Option("a", "address", false, "IP адрес")
        address: String? = null,
        @Option("t", "timeout", false, "Таймаут")
        timeout: String? = null
    ): String = runRequest { apiService.add(AddressListPayload(list, address, timeout)).execute() }

    @Command("edit", CommandType.COMMAND, "Редактирует адрес")
    fun commandEdit(
        @Option("i", "id", true, "ID правила")
        id: String,
        @Option("l", "list", true, "Название листа")
        list: String,
        @Option("a", "address", false, "IP адрес")
        address: String? = null,
        @Option("t", "timeout", false, "Таймаут")
        timeout: String? = null
    ): String = runRequest<AddressListsResponse> { apiService.edit(id, AddressListPayload(list, address, timeout)).execute() }

    @Command("disable", CommandType.COMMAND, "Отключить правило")
    fun commandDisable(
        @Option("i", "id", true, "ID правила")
        id: String
    ): String = runRequest<AddressListsResponse> { apiService.edit(id, AddressListPayload(disabled = true)).execute() }

    @Command("enable", CommandType.COMMAND, "Включить правило")
    fun commandEnable(
        @Option("i", "id", true, "ID правила")
        id: String
    ): String = runRequest<AddressListsResponse> { apiService.edit(id, AddressListPayload(disabled = false)).execute() }

    @Command("remove", CommandType.COMMAND, "Удалить элемент")
    fun commandRemove(
        @Option("i", "id", true, "Номер записи")
        id: String
    ): String = runRequest<Unit> { apiService.remove(id).execute() }
}
