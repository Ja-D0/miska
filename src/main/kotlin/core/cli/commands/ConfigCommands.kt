package com.microtik.core.cli.commands

import com.microtik.Microtik
import com.microtik.core.api.endpoints.Api

class ConfigCommands(
    override val path: String = "config",
    override val apiService: Api? = null
): AbstractCommands() {

    fun commandPrint(): String = Microtik.app.getConfig().toString()
}