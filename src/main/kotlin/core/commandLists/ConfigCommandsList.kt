package com.miska.core.commandLists

import com.miska.Miska
import com.miska.core.base.cli.CommandsListImpl
import com.miska.core.base.cli.annotations.Command
import com.miska.core.base.cli.annotations.CommandList
import com.miska.core.base.cli.annotations.CommandType


@CommandList("config")
class ConfigCommandsList : CommandsListImpl() {
    @Command("print", CommandType.COMMAND, "")
    fun commandPrint(): String = Miska.app.getConfig().toString()
}
