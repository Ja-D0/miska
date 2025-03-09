package com.microtik.core.base.cli.commandLists

import com.microtik.core.base.CommandsListImpl
import com.microtik.core.base.cli.annotations.Command
import com.microtik.core.base.cli.annotations.CommandList
import com.microtik.core.base.cli.annotations.CommandType

@CommandList("ip")
class IpCommandsList : CommandsListImpl() {

    @Command("firewall", CommandType.PATH, "")
    fun getFirewallCommands(): FirewallCommandsList = FirewallCommandsList()
}
