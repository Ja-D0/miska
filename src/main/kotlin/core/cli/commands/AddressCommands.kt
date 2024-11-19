package com.microtik.core.cli.commands

class AddressCommands(
    override val path: String = "address"
) : AbstractCommands() {

    fun commandPrint(): String = this::class.simpleName!!
}