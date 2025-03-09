package com.microtik.core.commandLists

import com.microtik.core.base.cli.CommandsListImpl
import com.microtik.core.base.cli.annotations.Command
import com.microtik.core.base.cli.annotations.CommandList
import com.microtik.core.base.cli.annotations.CommandType

@CommandList("ip")
class IpCommandsList : CommandsListImpl() {

    @Command("firewall", CommandType.PATH, "")
    fun getFirewallCommands(): FirewallCommandsList = FirewallCommandsList()
}
