package com.microtik.core.cli.commands

import com.microtik.core.api.endpoints.Api

class BaseCommands: AbstractCommands() {
    override val path: String = "."
    override val apiService: Api? = null

    fun getConfigCommands(): ConfigCommands = ConfigCommands()
    fun getAddressCommands(): AddressCommands = AddressCommands()
    fun getIpCommands(): IpCommands = IpCommands()
    fun commandPrint(address: String): String = address
}