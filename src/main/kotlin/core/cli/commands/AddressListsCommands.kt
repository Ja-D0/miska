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
        @Option("a", "address", true, "IP адрес")
        address: String
    ): String = runRequest { apiService.add(AddressListPayload(list, address)).execute() }

    @Command("remove", CommandType.COMMAND, "Удалить элемент")
    fun commandRemove(
        @Option("i", "id", true, "Номер записи")
        id: String
    ): String = runRequest<Unit> { apiService.remove(id).execute() }
}
