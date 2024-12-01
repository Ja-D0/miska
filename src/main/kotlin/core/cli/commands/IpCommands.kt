package com.microtik.core.cli.commands

import com.microtik.core.api.endpoints.Api

class IpCommands: AbstractCommands() {
    override val path: String = "ip"
    override val apiService: Api? = null

    fun getFirewallCommands(): FirewallCommands = FirewallCommands()
}