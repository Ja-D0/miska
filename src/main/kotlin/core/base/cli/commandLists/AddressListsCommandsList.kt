package com.microtik.core.base.cli.commandLists

import com.microtik.core.api.MicrotikApiService
import com.microtik.core.api.requestModels.AddressListPayload
import com.microtik.core.api.responseModels.AddressListsResponse
import com.microtik.core.base.CommandsListImpl
import com.microtik.core.base.cli.annotations.Command
import com.microtik.core.base.cli.annotations.CommandList
import com.microtik.core.base.cli.annotations.CommandOption
import com.microtik.core.base.cli.annotations.CommandType

@CommandList("address-list")
class AddressListsCommandsList : CommandsListImpl() {
    @Command("print", CommandType.COMMAND, "Show the elements")
    fun commandPrint(): String = runRequest<ArrayList<AddressListsResponse>> {
        MicrotikApiService.getInstance().getAddressListsApi().print().execute()
    }

    @Command("add", CommandType.COMMAND, "Adds to the list address")
    fun commandAdd(
        @CommandOption("l", "list", true, "The name of the list")
        list: String,
        @CommandOption("a", "address", false, "IP address")
        address: String? = null,
        @CommandOption("t", "timeout", false, "Timeout")
        timeout: String? = null
    ): String = runRequest {
        MicrotikApiService.getInstance().getAddressListsApi().add(AddressListPayload(list, address, timeout)).execute()
    }

    @Command("edit", CommandType.COMMAND, "Editing the address")
    fun commandEdit(
        @CommandOption("i", "id", true, "ID Rules")
        id: String,
        @CommandOption("l", "list", true, "The name of the list")
        list: String,
        @CommandOption("a", "address", false, "IP address")
        address: String? = null,
        @CommandOption("t", "timeout", false, "Timeout")
        timeout: String? = null
    ): String =
        runRequest<AddressListsResponse> {
            MicrotikApiService.getInstance().getAddressListsApi().edit(id, AddressListPayload(list, address, timeout))
                .execute()
        }

    @Command("disable", CommandType.COMMAND, "Turn off the rule")
    fun commandDisable(
        @CommandOption("i", "id", true, "ID Rules")
        id: String
    ): String = runRequest<AddressListsResponse> {
        MicrotikApiService.getInstance().getAddressListsApi().edit(id, AddressListPayload(disabled = true)).execute()
    }

    @Command("enable", CommandType.COMMAND, "Turn on the rule")
    fun commandEnable(
        @CommandOption("i", "id", true, "ID Rules")
        id: String
    ): String = runRequest<AddressListsResponse> {
        MicrotikApiService.getInstance().getAddressListsApi().edit(id, AddressListPayload(disabled = false)).execute()
    }

    @Command("remove", CommandType.COMMAND, "Remove the element")
    fun commandRemove(
        @CommandOption("i", "id", true, "Record number")
        id: String
    ): String = runRequest<Unit> { MicrotikApiService.getInstance().getAddressListsApi().remove(id).execute() }
}
