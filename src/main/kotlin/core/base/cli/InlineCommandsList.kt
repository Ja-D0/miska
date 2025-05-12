package com.miska.core.base.cli

import com.miska.Miska
import com.miska.core.base.cli.annotations.Command
import com.miska.core.base.cli.annotations.CommandOption
import com.miska.core.base.cli.annotations.CommandType
import org.apache.commons.cli.HelpFormatter
import kotlin.reflect.full.declaredFunctions
import kotlin.reflect.full.findAnnotation

class InlineCommandsList : CommandsListImpl() {

    @Command("exit", CommandType.COMMAND, "")
    fun commandExit() = Miska.app.stop()


    @Command("ls", CommandType.COMMAND, "")
    fun commandLs() {
        val result: StringBuilder = StringBuilder()

        Miska.app.getCurrentCommandsList()::class.declaredFunctions
            .sortedWith(compareBy(
                { it.findAnnotation<Command>()?.commandType != CommandType.PATH },
                { it.name }
            )).forEach { member ->
                result.append(member.findAnnotation<Command>()?.id + "   ")
            }

        return Miska.app.cliOut(result.toString())
    }

    @Command("..", CommandType.COMMAND, "")
    fun commandBack() {
        Miska.app.goBack()
    }
    
    @Command("help", CommandType.COMMAND, "")
    fun commandHelp(
        @CommandOption("i", "id", true, "")
        commandId: String
    ) {
        val command = Miska.app.getCurrentCommandsList().createCommand(commandId)

        val formatter = HelpFormatter()
        formatter.printHelp(command.id, command.extractOptionsAsOptions())
    }
}