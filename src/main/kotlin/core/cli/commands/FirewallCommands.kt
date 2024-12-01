package com.microtik.core.cli.commands

import com.microtik.core.api.endpoints.Api

class FirewallCommands(
    override val path: String = "firewall",
    override val apiService: Api? = null
): AbstractCommands() {

    fun getFilterCommands(): FilterCommands = FilterCommands()
}