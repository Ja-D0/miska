package com.microtik.core.cli.commands

import com.microtik.Microtik

class ConfigCommands(
    override val path: String = "config"
): AbstractCommands() {

    fun commandPrint(): String = Microtik.app.getConfig().toString()
}