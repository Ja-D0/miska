package com.microtik.core.cli.CliManager

import com.microtik.Microtik
import com.microtik.core.cli.annotations.Command
import com.microtik.core.cli.annotations.CommandType
import com.microtik.core.cli.commandExecutor.CommandExecutor
import com.microtik.core.cli.commands.AbstractCommands
import com.microtik.core.cli.commands.BaseCommands
import com.microtik.core.cli.exceptions.PathConflictException
import org.apache.commons.cli.*
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberFunctions

class CliManager {
    private var currentCommands: AbstractCommands = BaseCommands()
//    private val baseCliCommands: AbstractCommands = BaseCliCommands()
    private val traceCommands: MutableList<AbstractCommands> = mutableListOf()
    private var currentPath: String
    private lateinit var commands: List<String>
    private lateinit var options: List<String>

    init {
        currentPath = currentCommands.path
    }

    fun parseCommandLine(commandLine: String): String
    {
        val preparedCommandLine = prepareCommand(commandLine)
        val parts = preparedCommandLine.split(" ")
        commands = parts.first().split("/")
        options = parts.drop(1)

        return execute()
    }

    private fun execute(): String
    {
        var result: String = ""

        for (command in commands) {
            if (checkAndRunBasicCommands(command, options)) {
                continue
            }

            if (isPath(command)) {
                goToCommands(CommandExecutor<AbstractCommands>(CommandType.PATH) { commandName, error, options ->
                    printHelp(commandName, error, options)
                }.execute(command, arrayListOf(), currentCommands))
                continue
            }

            result = CommandExecutor<String>(CommandType.COMMAND) { commandName, error, options ->
                printHelp(commandName, error, options)
            }.execute(command, options, currentCommands)
        }

        return result
    }

    private fun checkAndRunBasicCommands(command: String, options: List<String>): Boolean
    {
        return when (command) {
            ".." -> {
                goBack()
                true
            }
            "exit" -> {
                Microtik.app.stop()
                throw Exception("Приложение остановлено")
            }
            else -> false
        }
    }

    fun goToCommands(newCommand: AbstractCommands): Unit
    {
        traceCommands.add(currentCommands)
        currentCommands = newCommand
        currentPath += "/${newCommand.path}"
    }

    fun goBack(): Unit
    {
        if (traceCommands.isNotEmpty()) {
            currentPath = currentPath.dropLast(currentCommands.path.length + 1)
            currentCommands = traceCommands.last()
            traceCommands.removeLast()
        }
    }

    private fun isPath(command: String): Boolean
    {
        return try {
            currentCommands::class.memberFunctions.single {function ->
                val commandAnnotation = function.findAnnotation<Command>()
                commandAnnotation != null
                        && commandAnnotation.commandType == CommandType.PATH
                        && commandAnnotation.name == command
            }
            true
        } catch (illegalArgumentException: IllegalArgumentException) {
            throw PathConflictException("Невозможно определить правильный путь: $command")
        } catch (notFoundException: NoSuchElementException) {
            false
        }
    }

    fun cliOut(text: String): Unit = println(text)

    fun cliIn(text: String?): String?
    {
        if (text == null) {
            print(">>> $currentPath$ ")
        } else {
            print(">>> $text: ")
        }

        return readlnOrNull()
    }

    private fun prepareCommand(command: String): String
    {
        var normalizedCommand: String = command

        if (command.last() == '/') {
             normalizedCommand = command.dropLast(1)
        }

        return normalizedCommand.trim()
    }

    private fun printHelp(commandName: String, errorMessage: String? = null, options: Options): Unit
    {
        if (errorMessage != null) {
            println(errorMessage)
        }

        val formatter = HelpFormatter()
        formatter.printHelp(commandName, options)
    }
}