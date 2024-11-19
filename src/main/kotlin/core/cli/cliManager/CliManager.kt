package com.microtik.core.cli.CliManager

import com.microtik.Microtik
import com.microtik.core.cli.commands.AbstractCommands
import com.microtik.core.cli.commands.BaseCommands

class CliManager {
    private var currentCommands: AbstractCommands = BaseCommands()
    private val traceCommands: MutableList<AbstractCommands> = mutableListOf()
    private var currentPath: String

    init {
        currentPath = currentCommands.path
    }

    fun executeCommand(command: String): String?
    {
        val normalizedCommand = prepareCommand(command)

        val mathResult = Regex("""^say -v '(.*)'""").find(normalizedCommand)

        if (mathResult != null && !mathResult.groups[1]?.value.isNullOrBlank()) {
            return  mathResult.groups[1]?.value.toString()
        }

        var result: Any? = null

        normalizedCommand.split("/").forEach {
            if (it == "..") {
                goBack()
                return@forEach
            }

            if (it == "exit") {
                Microtik.app.stop()
                return null
            }

            result = currentCommands.execute(it)

            if (result is AbstractCommands) {
                goToCommands(result as AbstractCommands)
                result = null
            }
        }

        if (result == null) return result

        return result as String
    }

    private fun goToCommands(newCommand: AbstractCommands)
    {
        traceCommands.add(currentCommands)
        currentCommands = newCommand
        currentPath += "/${newCommand.path}"
    }

    private fun goBack()
    {
        if (traceCommands.isNotEmpty()) {
            currentPath = currentPath.dropLast(currentCommands.path.length + 1)
            currentCommands = traceCommands.last()
            traceCommands.removeLast()
        }
    }

    fun cliOut(text: String): Unit = println(">>> $text")

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
}