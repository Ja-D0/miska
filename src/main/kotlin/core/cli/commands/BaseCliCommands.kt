package com.microtik.core.cli.commands

import com.microtik.core.api.endpoints.Api
import com.microtik.core.cli.CliManager.CliManager
import com.microtik.core.cli.annotations.Command
import com.microtik.core.cli.annotations.CommandType

class BaseCliCommands(
    private val cliManager: CliManager,
): AbstractCommands() {
    override val path: String = ""
    override val apiService: Api? = null
//
//    @Command("..", CommandType.COMMAND, "")
//    fun goToCommands(newCommand: AbstractCommands): Unit
//    {
//        cliManager.g
//    }
//
//    @Command("exit", CommandType.COMMAND, "")
//    fun goBack(): Unit
//    {
//        if (traceCommands.isNotEmpty()) {
//            currentPath = currentPath.dropLast(currentCommands.path.length + 1)
//            currentCommands = traceCommands.last()
//            traceCommands.removeLast()
//        }
//    }
}