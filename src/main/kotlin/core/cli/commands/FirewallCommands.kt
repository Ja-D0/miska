package com.microtik.core.cli.commands

import com.microtik.core.api.endpoints.Api
import com.microtik.core.cli.annotations.Command
import com.microtik.core.cli.annotations.CommandType

class FirewallCommands: AbstractCommands() {
    override val path: String = "firewall"
    override val apiService: Api? = null

    @Command("filter", CommandType.PATH, "")
    fun getFilterCommands(): FilterCommands = FilterCommands()

    @Command("address-list", CommandType.PATH, "")
    fun getAddressListsCommands(): AddressListsCommands = AddressListsCommands()
}