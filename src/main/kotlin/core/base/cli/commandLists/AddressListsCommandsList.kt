package com.microtik.core.base.cli.commandLists

import com.microtik.Microtik
import com.microtik.core.api.MicrotikApiService
import com.microtik.core.api.requestModels.AddressListPayload
import com.microtik.core.api.responseModels.AddressListsResponse
import com.microtik.core.base.CommandsListImpl
import com.microtik.core.base.cli.annotations.Command
import com.microtik.core.base.cli.annotations.CommandList
import com.microtik.core.base.cli.annotations.CommandOption
import com.microtik.core.base.cli.annotations.CommandType
import java.io.File

@CommandList("address-list")
class AddressListsCommandsList : CommandsListImpl() {
    @Command("print", CommandType.COMMAND, "Show the elements")
    fun commandPrint(): String =
        MicrotikApiService.runRequest<ArrayList<AddressListsResponse>> {
            MicrotikApiService.getInstance().getAddressListsApi().print().execute()
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
        MicrotikApiService.runRequest {
            MicrotikApiService.getInstance().getAddressListsApi().add(AddressListPayload(list, address, timeout))
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
        MicrotikApiService.runRequest {
            MicrotikApiService.getInstance().getAddressListsApi().edit(id, AddressListPayload(list, address, timeout))
                .execute()
        }.toString()

    @Command("disable", CommandType.COMMAND, "Turn off the rule")
    fun commandDisable(
        @CommandOption("i", "id", true, "ID Rules")
        id: String
    ): String =
        MicrotikApiService.runRequest {
            MicrotikApiService.getInstance().getAddressListsApi().edit(id, AddressListPayload(disabled = true))
                .execute()
        }.toString()

    @Command("enable", CommandType.COMMAND, "Turn on the rule")
    fun commandEnable(
        @CommandOption("i", "id", true, "ID Rules")
        id: String
    ): String =
        MicrotikApiService.runRequest<AddressListsResponse> {
            MicrotikApiService.getInstance().getAddressListsApi().edit(id, AddressListPayload(disabled = false))
                .execute()
        }.toString()

    @Command("remove", CommandType.COMMAND, "Remove the element")
    fun commandRemove(
        @CommandOption("i", "id", true, "Record number")
        id: String
    ): Unit =
        MicrotikApiService.runRequest<Unit> {
            MicrotikApiService.getInstance().getAddressListsApi().remove(id).execute()
        }

    @Command("load-from-file", CommandType.COMMAND, "Load IP from file")
    fun commandLoadFromFile(
        @CommandOption("f", "filepath", true, "Path to the file")
        fileName: String,
        @CommandOption("l", "list", true, "Name of the list; if it does not exist, it will be created automatically")
        list: String
    ): String {
        val addressLists =
            MicrotikApiService.runRequest {
                MicrotikApiService.getInstance().getAddressListsApi().print(list).execute()
            }

        if (addressLists!!.isEmpty()) {
            Microtik.app.cliOut("The list \"$list\" does not exist; it will be created automatically")
        }

        val regStr = "^([0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3})(\\/(3[0-2]|[12]?[0-9]))?\$"
        val ipRegex = Regex(regStr)
        var lineIndex = 1

        File(Microtik.getBaseJarDir() + File.separator + "configs" + File.separator + "ip" + File.separator + fileName).forEachLine { address ->
            if (ipRegex.matches(address)) {
                val addressList = MicrotikApiService.runRequest {
                    MicrotikApiService.getInstance().getAddressListsApi().add(AddressListPayload(list, address))
                        .execute()
                }

                if (addressList != null) {
                    Microtik.app.cliOut("IP address ${addressList.address} added to the list \"${addressList.list}\"")
                } else {
                    Microtik.app.cliOut("Error adding IP address $address to the list $list")
                }
            } else {
                Microtik.app.cliOut("IP address $address is not valid on line $lineIndex. Skipping.")
            }

            lineIndex++
        }

        return "File processing completed"
    }
}
