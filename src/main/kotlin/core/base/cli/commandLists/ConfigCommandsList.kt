package com.microtik.core.base.cli.commandLists

import com.microtik.Microtik
import com.microtik.core.base.CommandsListImpl
import com.microtik.core.base.cli.annotations.Command
import com.microtik.core.base.cli.annotations.CommandList
import com.microtik.core.base.cli.annotations.CommandType

@CommandList("config")
class ConfigCommandsList : CommandsListImpl() {

    @Command("print", CommandType.COMMAND, "")
    fun commandPrint(): String = Microtik.app.getConfig().toString()
}
