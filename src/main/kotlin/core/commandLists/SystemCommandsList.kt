package com.miska.core.commandLists

import com.miska.core.base.cli.CommandsListImpl
import com.miska.core.base.cli.annotations.Command
import com.miska.core.base.cli.annotations.CommandList
import com.miska.core.base.cli.annotations.CommandType

@CommandList("system")
class SystemCommandsList : CommandsListImpl() {
    @Command("config", CommandType.PATH, "Application configuration")
    fun getConfigCommands(): ConfigCommandsList = ConfigCommandsList()

    @Command("server", CommandType.PATH, "REST API server")
    fun getServerCommands(): ServerCommandsList = ServerCommandsList()
}