package com.microtik.core.cli.commands

import com.microtik.core.exceptions.NotFoundCommandException
import kotlin.reflect.KFunction
import kotlin.reflect.full.declaredFunctions
import kotlin.reflect.full.functions

abstract class AbstractCommands {
    abstract val path: String

    fun commandHelp(): String
    {
        val result: StringBuilder = StringBuilder()

        this::class.declaredFunctions
            .sortedWith(compareBy(
                { !it.name.startsWith("get") },
                { it.name }
            )).forEach { member ->
            result.append(member.name
                .replace("command", "")
                .replace("Commands", "")
                .replace("get", "")
                .replaceFirstChar { it.lowercase() } + "   ")
        }

        return result.toString().trim()
    }

    fun findCommands(command: String): AbstractCommands? {
        val convertedCommand: String = command.replaceFirstChar { char -> char.uppercase() }
        val declaredFunctions: Collection<KFunction<*>> = this::class.functions

        return try {
            declaredFunctions.single {
                it.name == "get" + convertedCommand + "Commands"
            }.call(this) as AbstractCommands
        } catch (_: NoSuchElementException) {
            null
        }
    }

    fun findCommandToExecute(command: String): KFunction<*> {
        val convertedCommand: String = command.replaceFirstChar { char -> char.uppercase() }
        val declaredFunctions: Collection<KFunction<*>> = this::class.functions

        return try {
            declaredFunctions.single {
                it.name == "command$convertedCommand"
            }
        } catch (_: NoSuchElementException) {
            throw NotFoundCommandException("Команда не найдена: $command")
        }
    }

}

