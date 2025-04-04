package com.miska.core.commandLists

import com.miska.core.base.cli.CommandsListImpl
import com.miska.core.base.cli.annotations.Command
import com.miska.core.base.cli.annotations.CommandList
import com.miska.core.base.cli.annotations.CommandType

@CommandList("firewall")
class FirewallCommandsList : CommandsListImpl() {

    @Command("filter", CommandType.PATH, "")
    fun getFilterCommands(): FilterCommandsList = FilterCommandsList()

    @Command("address-list", CommandType.PATH, "")
    fun getAddressListsCommands(): AddressListsCommandsList = AddressListsCommandsList()
}
