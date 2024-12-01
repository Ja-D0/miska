package com.microtik.core.cli.commands

import com.microtik.core.api.endpoints.Api

class FilterCommands(
    override val path: String = "filter",
    override val apiService: Api? = null
): AbstractCommands() {

    fun commandPrint(): String
    {
        return path
    }
}