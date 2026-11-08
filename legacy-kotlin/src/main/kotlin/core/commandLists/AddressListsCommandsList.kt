package com.miska.core.commandLists

import com.miska.Miska
import com.miska.core.api.MikrotikApiService
import com.miska.core.api.requestModels.AddressListPayload
import com.miska.core.api.responseModels.AddressListsResponse
import com.miska.core.base.cli.CommandsListImpl
import com.miska.core.base.cli.annotations.Command
import com.miska.core.base.cli.annotations.CommandList
import com.miska.core.base.cli.annotations.CommandOption
import com.miska.core.base.cli.annotations.CommandType
import java.io.File

@CommandList("address-list")
class AddressListsCommandsList : CommandsListImpl() {
    @Command("print", CommandType.COMMAND, "Show the elements")
    fun commandPrint(): String =
        MikrotikApiService.runRequest<ArrayList<AddressListsResponse>> {
            MikrotikApiService.getInstance().getAddressListsApi().print().execute()
        }.joinToString("\n") { it.toString() }


    @Command("add", CommandType.COMMAND, "Adds to the list address")
    fun commandAdd(
        @CommandOption("l", "list", true, "The name of the list")
        list: String,
        @CommandOption("a", "address", false, "IP address")
        address: String? = null,
        @CommandOption("t", "timeout", false, "Timeout")
        timeout: String? = null
    ): String =
        MikrotikApiService.runRequest {
            MikrotikApiService.getInstance().getAddressListsApi().add(AddressListPayload(list, address, timeout))
                .execute()
        }.toString()

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
        MikrotikApiService.runRequest {
            MikrotikApiService.getInstance().getAddressListsApi().edit(id, AddressListPayload(list, address, timeout))
                .execute()
        }.toString()

    @Command("disable", CommandType.COMMAND, "Turn off the rule")
    fun commandDisable(
        @CommandOption("i", "id", true, "ID Rules")
        id: String
    ): String =
        MikrotikApiService.runRequest {
            MikrotikApiService.getInstance().getAddressListsApi().edit(id, AddressListPayload(disabled = true))
                .execute()
        }.toString()

    @Command("enable", CommandType.COMMAND, "Turn on the rule")
    fun commandEnable(
        @CommandOption("i", "id", true, "ID Rules")
        id: String
    ): String =
        MikrotikApiService.runRequest<AddressListsResponse> {
            MikrotikApiService.getInstance().getAddressListsApi().edit(id, AddressListPayload(disabled = false))
                .execute()
        }.toString()

    @Command("remove", CommandType.COMMAND, "Remove the element")
    fun commandRemove(
        @CommandOption("i", "id", true, "Record number")
        id: String
    ): Unit =
        MikrotikApiService.runRequest<Unit> {
            MikrotikApiService.getInstance().getAddressListsApi().remove(id).execute()
        }

    @Command("load-from-file", CommandType.COMMAND, "Load IP from file")
    fun commandLoadFromFile(
        @CommandOption("f", "filepath", true, "Path to the file")
        fileName: String,
        @CommandOption("l", "list", true, "Name of the list; if it does not exist, it will be created automatically")
        list: String
    ): String {
        val addressLists =
            MikrotikApiService.runRequest {
                MikrotikApiService.getInstance().getAddressListsApi().print(list).execute()
            }

        if (addressLists!!.isEmpty()) {
            Miska.app.cliOut("The list \"$list\" does not exist; it will be created automatically")
        }

        val regStr = "^([0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3})(\\/(3[0-2]|[12]?[0-9]))?\$"
        val ipRegex = Regex(regStr)
        var lineIndex = 1

        File(Miska.getBaseJarDir() + File.separator + "configs" + File.separator + "ip" + File.separator + fileName).forEachLine { address ->
            if (ipRegex.matches(address)) {
                val addressList = MikrotikApiService.runRequest {
                    MikrotikApiService.getInstance().getAddressListsApi().add(AddressListPayload(list, address))
                        .execute()
                }

                if (addressList != null) {
                    Miska.app.cliOut("IP address ${addressList.address} added to the list \"${addressList.list}\"")
                } else {
                    Miska.app.cliOut("Error adding IP address $address to the list $list")
                }
            } else {
                Miska.app.cliOut("IP address $address is not valid on line $lineIndex. Skipping.")
            }

            lineIndex++
        }

        return "File processing completed"
    }
}
