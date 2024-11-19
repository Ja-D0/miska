package com.microtik.core.cli.commands

import com.microtik.core.cli.commands.AbstractCommands

class AddressCommands(
    override val path: String = "address/"
) : AbstractCommands() {

    fun commandPrint(): String = this::class.simpleName!!
}