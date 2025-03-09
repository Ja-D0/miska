package com.microtik.core.commandLists

import com.microtik.core.base.cli.CommandsListImpl
import com.microtik.core.base.cli.annotations.Command
import com.microtik.core.base.cli.annotations.CommandList
import com.microtik.core.base.cli.annotations.CommandType

@CommandList("firewall")
class FirewallCommandsList : CommandsListImpl() {

    @Command("filter", CommandType.PATH, "")
    fun getFilterCommands(): FilterCommandsList = FilterCommandsList()

    @Command("address-list", CommandType.PATH, "")
    fun getAddressListsCommands(): AddressListsCommandsList = AddressListsCommandsList()
}
