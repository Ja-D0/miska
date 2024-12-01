package com.microtik.core.cli.commands

import com.microtik.core.api.endpoints.Api

class FilterCommands: AbstractCommands() {
    override val path: String = "filter"
    override val apiService: Api? = null

    fun commandPrint(): String
    {
        return path
    }
}