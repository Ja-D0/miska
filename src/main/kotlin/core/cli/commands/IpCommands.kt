package com.microtik.core.cli.commands

import com.microtik.core.api.endpoints.Api
import com.microtik.core.cli.annotations.Command
import com.microtik.core.cli.annotations.CommandType

class IpCommands : AbstractCommands() {
    override val path: String = "ip"
    override val apiService: Api? = null

    @Command("firewall", CommandType.PATH, "")
    fun getFirewallCommands(): FirewallCommands = FirewallCommands()
}
