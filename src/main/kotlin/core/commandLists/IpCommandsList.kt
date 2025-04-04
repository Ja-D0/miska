package com.miska.core.commandLists

import com.miska.core.base.cli.CommandsListImpl
import com.miska.core.base.cli.annotations.Command
import com.miska.core.base.cli.annotations.CommandList
import com.miska.core.base.cli.annotations.CommandType

@CommandList("ip")
class IpCommandsList : CommandsListImpl() {

    @Command("firewall", CommandType.PATH, "")
    fun getFirewallCommands(): FirewallCommandsList = FirewallCommandsList()
}
