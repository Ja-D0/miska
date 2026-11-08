package com.miska.core.commandLists

import com.miska.core.api.MikrotikApiService
import com.miska.core.api.requestModels.AddressPayload
import com.miska.core.api.responseModels.AddressResponse
import com.miska.core.base.cli.CommandsListImpl
import com.miska.core.base.cli.annotations.Command
import com.miska.core.base.cli.annotations.CommandList
import com.miska.core.base.cli.annotations.CommandOption
import com.miska.core.base.cli.annotations.CommandType

/**
 *
 */
@CommandList("address")
class AddressCommandsList : CommandsListImpl() {

    @Command("print", CommandType.COMMAND, "Show the elements")
    fun commandPrint(): String =
        MikrotikApiService.runRequest<ArrayList<AddressResponse>> {
            MikrotikApiService.getInstance().getAddressApi().print().execute()
        }.joinToString("\n") { it.toString() }

    @Command("add", CommandType.COMMAND, "Adds the address")
    fun commandAdd(
        @CommandOption("a", "address", true, "IP address")
        address: String,
        @CommandOption("n", "network", false, "Network")
        network: String? = null,
        @CommandOption("i", "interface", true, "Interface")
        interfaces: String
    ): String =
        MikrotikApiService.runRequest {
            MikrotikApiService.getInstance().getAddressApi().add(AddressPayload(address, interfaces, network)).execute()
        }.toString()

    @Command("edit", CommandType.COMMAND, "Editing the address")
    fun commandEdit(
        @CommandOption("i", "id", true, "ID Rules")
        id: String,
        @CommandOption("a", "address", false, "IP address")
        address: String? = null,
        @CommandOption("n", "network", false, "Network")
        network: String? = null,
        @CommandOption("i", "interface", false, "Interface")
        interfaces: String? = null
    ): String =
        MikrotikApiService.runRequest<AddressResponse> {
            MikrotikApiService.getInstance().getAddressApi().edit(id, AddressPayload(address, interfaces, network))
                .execute()
        }.toString()

    @Command("disable", CommandType.COMMAND, "Turn off the rule")
    fun commandDisable(
        @CommandOption("i", "id", true, "ID Rules")
        id: String
    ): String =
        MikrotikApiService.runRequest<AddressResponse> {
            MikrotikApiService.getInstance().getAddressApi().edit(id, AddressPayload(disabled = true)).execute()
        }.toString()

    @Command("enable", CommandType.COMMAND, "Turn on the rule")
    fun commandEnable(
        @CommandOption("i", "id", true, "ID Rules")
        id: String
    ): String =
        MikrotikApiService.runRequest<AddressResponse> {
            MikrotikApiService.getInstance().getAddressApi().edit(id, AddressPayload(disabled = false)).execute()
        }.toString()

    @Command("remove", CommandType.COMMAND, "Remove the element")
    fun commandRemove(
        @CommandOption("i", "id", true, "Record number")
        id: String
    ): Unit =
        MikrotikApiService.runRequest<Unit> { MikrotikApiService.getInstance().getAddressApi().remove(id).execute() }
}
