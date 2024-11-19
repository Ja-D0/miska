package com.microtik.core.cli.commands

import com.microtik.core.exceptions.NotFoundCommandException
import kotlin.reflect.KCallable
import kotlin.reflect.KFunction
import kotlin.reflect.full.declaredFunctions
import kotlin.reflect.full.functions

abstract class AbstractCommands: Executable {
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

    override fun execute(command: String): Any? = defineCommand(command).call(this)

    private fun defineCommand(command: String): KCallable<Any?>
    {
        val convertedCommand: String = command.replaceFirstChar { char -> char.uppercase() }
        val declaredFunctions: Collection<KFunction<*>> = this::class.functions

        val func = try {
            declaredFunctions.single {
                it.name == "command$convertedCommand"
            }
        } catch (_: NoSuchElementException) {
            try {
                declaredFunctions.single {
                    it.name == "get" + convertedCommand + "Commands"
                }
            } catch (_: NoSuchElementException) {
                throw NotFoundCommandException("Команда не найдена: $command")
            }
        }
        return func
    }
}

