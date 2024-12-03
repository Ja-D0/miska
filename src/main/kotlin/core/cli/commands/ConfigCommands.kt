package com.microtik.core.cli.commands

import com.microtik.Microtik
import com.microtik.core.api.endpoints.Api
import com.microtik.core.cli.annotations.Command
import com.microtik.core.cli.annotations.CommandType

class ConfigCommands: AbstractCommands() {
    override val path: String = "config"
    override val apiService: Api? = null

    @Command("print", CommandType.COMMAND, "Выводит конфиг")
    fun commandPrint(): String = Microtik.app.getConfig().toString()
}