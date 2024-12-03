package com.microtik.core.cli.commands

import com.microtik.core.api.endpoints.Api
import com.microtik.core.cli.annotations.Command
import com.microtik.core.cli.annotations.CommandType
import com.microtik.core.cli.annotations.Option

class BaseCommands: AbstractCommands() {
    override val path: String = "."
    override val apiService: Api? = null

    @Command("config", CommandType.PATH, "")
    fun getConfigCommands(): ConfigCommands = ConfigCommands()

    @Command("address", CommandType.PATH, "")
    fun getAddressCommands(): AddressCommands = AddressCommands()

    @Command("ip", CommandType.PATH, "")
    fun getIpCommands(): IpCommands = IpCommands()

    @Command("print", CommandType.COMMAND, "Печатает")
    fun commandPrint(
        @Option("a", "address", true, "IP адрес")
        address: String
    ): String = address
}