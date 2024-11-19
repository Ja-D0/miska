package com.microtik.core.cli.commands

import com.microtik.core.cli.commands.AbstractCommands

class ConfigCommands(
    override val path: String = "config/"
): AbstractCommands() {

    fun commandPrint(): String = this::class.simpleName!!
}