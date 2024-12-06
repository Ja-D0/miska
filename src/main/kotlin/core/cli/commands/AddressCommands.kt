package com.microtik.core.cli.commands

import com.microtik.core.api.MicrotikApiService
import com.microtik.core.api.endpoints.AddressApi
import com.microtik.core.api.requestModels.AddressPayload
import com.microtik.core.api.responseModels.AddressResponse
import com.microtik.core.cli.annotations.Command
import com.microtik.core.cli.annotations.CommandType
import com.microtik.core.cli.annotations.Option

class AddressCommands : AbstractCommands() {
    override val path: String = "address"
    override val apiService: AddressApi = MicrotikApiService.getInstance().getAddressApi()

    @Command("print", CommandType.COMMAND, "Показать элементы")
    fun commandPrint(): String = runRequest<ArrayList<AddressResponse>> { apiService.print().execute() }

    @Command("add", CommandType.COMMAND, "Добавляет адрес")
    fun commandAdd(
        @Option("a", "address", true, "IP адрес")
        address: String,
        @Option("i", "interface", true, "Интерфейс")
        interfaces: String
    ): String = runRequest { apiService.add(AddressPayload(address, interfaces)).execute() }

    @Command("edit", CommandType.COMMAND, "Редактирует адрес")
    fun commandEdit(
        @Option("i", "id", true, "ID правила")
        id: String,
        @Option("a", "address", false, "IP адрес")
        address: String? = null,
        @Option("i", "interface", false, "Интерфейс")
        interfaces: String? = null
    ): String = runRequest<AddressResponse> { apiService.edit(id, AddressPayload(address, interfaces)).execute() }

    @Command("disable", CommandType.COMMAND, "Отключить правило")
    fun commandDisable(
        @Option("i", "id", true, "ID правила")
        id: String
    ): String = runRequest<AddressResponse> { apiService.edit(id, AddressPayload(disabled = true)).execute() }

    @Command("enable", CommandType.COMMAND, "Включить правило")
    fun commandEnable(
        @Option("i", "id", true, "ID правила")
        id: String
    ): String = runRequest<AddressResponse> { apiService.edit(id, AddressPayload(disabled = false)).execute() }

    @Command("remove", CommandType.COMMAND, "Удалить элемент")
    fun commandRemove(
        @Option("i", "id", true, "Номер записи")
        id: String
    ): String = runRequest<Unit> { apiService.remove(id).execute() }
}
