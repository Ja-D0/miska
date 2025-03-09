package com.microtik.core.base.cli

import com.microtik.Microtik
import com.microtik.core.base.cli.annotations.Command
import com.microtik.core.base.cli.annotations.CommandOption
import com.microtik.core.base.cli.annotations.CommandType
import com.microtik.core.base.cli.exceptions.PathConflictException
import org.apache.commons.cli.HelpFormatter
import kotlin.reflect.full.declaredFunctions
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberFunctions

/**
 *
 */
class InlineCommandsList : CommandsListImpl() {

    /**
     *
     */
    @Command("ls", CommandType.COMMAND, "")
    fun commandLs() {
        val result: StringBuilder = StringBuilder()

        Microtik.app.getCurrentCommandsList()::class.declaredFunctions
            .sortedWith(compareBy(
                { it.findAnnotation<Command>()?.commandType != CommandType.PATH },
                { it.name }
            )).forEach { member ->
                result.append(member.findAnnotation<Command>()?.id + "   ")
            }

        return Microtik.app.cliOut(result.toString())
    }

    /**
     *
     */
    @Command("..", CommandType.COMMAND, "")
    fun commandBack() {
        Microtik.app.goBack()
    }

    /**
     *
     */
    @Command("help", CommandType.COMMAND, "")
    fun commandHelp(
        @CommandOption("i", "id", true, "")
        commandId: String
    ) {
        val command = Microtik.app.getCurrentCommandsList().createCommand(commandId)

        val formatter = HelpFormatter()
        formatter.printHelp(command.id, command.extractOptions())
    }

    /**
     *
     */
    fun isInlineCommand(commandId: String): Boolean {
        return try {
            this::class.memberFunctions.single { function ->
                val commandAnnotation = function.findAnnotation<Command>()
                commandAnnotation != null
                        && commandAnnotation.commandType == CommandType.COMMAND
                        && commandAnnotation.id == commandId
            }
            true
        } catch (illegalArgumentException: IllegalArgumentException) {
            throw PathConflictException("It is impossible to determine the right path: $commandId")
        } catch (notFoundException: NoSuchElementException) {
            false
        }
    }
}