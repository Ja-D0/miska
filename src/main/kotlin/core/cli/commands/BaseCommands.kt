package com.microtik.core.cli.commands

class BaseCommands(
    override val path: String = "."
): AbstractCommands() {

    fun getConfigCommands(): ConfigCommands = ConfigCommands()
    fun getAddressCommands(): AddressCommands = AddressCommands()
    fun commandPrint(address: String): String = address
}