package com.microtik.core.cli.commands

import com.microtik.core.api.endpoints.Api

class FirewallCommands: AbstractCommands() {
    override val path: String = "firewall"
    override val apiService: Api? = null

    fun getFilterCommands(): FilterCommands = FilterCommands()
    fun getAddressListsCommands(): AddressListsCommands = AddressListsCommands()
}