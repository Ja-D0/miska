package com.microtik.core.cli.commands

import com.microtik.core.api.endpoints.Api
import com.microtik.core.cli.annotations.Command
import com.microtik.core.cli.annotations.CommandType

class FilterCommands: AbstractCommands() {
    override val path: String = "filter"
    override val apiService: Api? = null

    @Command("print", CommandType.COMMAND, "")
    fun commandPrint(): String
    {
        return path
    }
}