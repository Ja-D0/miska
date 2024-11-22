package com.microtik.core.cli.CliManager

import com.microtik.Microtik
import com.microtik.core.cli.commands.AbstractCommands
import com.microtik.core.cli.commands.BaseCommands
import org.apache.commons.cli.*
import kotlin.reflect.KFunction

class CliManager {
    private var commandLineOptions: Options = Options()
    private var currentCommands: AbstractCommands = BaseCommands()
    private val traceCommands: MutableList<AbstractCommands> = mutableListOf()
    private var currentPath: String

    init {
        currentPath = currentCommands.path
    }

    fun executeCommand(command: String): String? {
        var normalizedCommand = prepareCommand(command)
        val index = normalizedCommand.indexOf(' ')
        var clParams: List<String> = listOf()

        if (index != -1) {
            normalizedCommand = normalizedCommand.take(index)
        }

        var execCommand: KFunction<*>? = null
        var execResult: String? = null

        normalizedCommand.split("/").forEach {
            if (it == "..") {
                goBack()
                return null
            }

            if (it == "exit") {
                Microtik.app.stop()
                return null
            }

            val commands = currentCommands.findCommands(it)

            if (commands != null) {
                goToCommands(commands)
            } else {
                execCommand = currentCommands.findCommandToExecute(it)
                clParams = parseClParams(command, execCommand!!)
                return@forEach
            }
        }

        try {
            execResult = execCommand!!.call(currentCommands, *clParams.toTypedArray()) as String
        } catch (e: Exception) {
            printHelp(normalizedCommand.last().toString(), getCommandOptions(execCommand!!))
            throw Exception()
        }

        return execResult
    }

    private fun goToCommands(newCommand: AbstractCommands): Unit
    {
        traceCommands.add(currentCommands)
        currentCommands = newCommand
        currentPath += "/${newCommand.path}"
    }

    private fun goBack(): Unit
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

    private fun parseClParams(command: String, function: KFunction<*>): List<String>
    {
        val params = function.parameters
        val options: Options = getCommandOptions(function)
        val commandName = command.split(" ").first()
        val commandLineParams = command.split(" ").drop(1).toTypedArray()

        val parser = DefaultParser()

        return try {
            val cmd: CommandLine = parser.parse(options, commandLineParams)

            if (cmd.hasOption("h")) {
                printHelp(commandName, options)
            }

            params.mapNotNull { param ->
                cmd.getOptionValue(param.name)
            }

        } catch (e: ParseException) {
            throw e
        }
    }

    private fun printHelp(commandName: String, options: Options): Unit
    {
        val formatter = HelpFormatter()
        formatter.printHelp(commandName, options)
    }

    private fun getCommandOptions(function: KFunction<*>): Options
    {
        val options: Options = Options()
        val params = function.parameters

        for (param in params) {
            val name = param.name ?: continue
            options.addOption(Option(name.first().toString(), name, true, ""))
        }

        options.addOption("h", "help", false, "Показать справку")

        return options
    }
}