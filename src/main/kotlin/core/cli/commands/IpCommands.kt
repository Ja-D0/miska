package com.microtik.core.cli.commands

import com.microtik.core.api.endpoints.Api

class IpCommands(
    override val path: String = "ip",
    override val apiService: Api? = null
) : AbstractCommands() {

    fun getFirewallCommands(): FirewallCommands = FirewallCommands()
}