package com.microtik.core.cli.commands

import com.microtik.core.api.endpoints.Api
import com.microtik.core.cli.annotations.Command
import com.microtik.core.cli.annotations.CommandType
import kotlin.reflect.full.declaredFunctions
import kotlin.reflect.full.findAnnotation

abstract class AbstractCommands {
    abstract val path: String
    abstract val apiService: Api?

    @Command("help", CommandType.COMMAND, "Выводит команды текущего каталога")
    fun commandHelp(): String
    {
        val result: StringBuilder = StringBuilder()

        this::class.declaredFunctions
            .sortedWith(compareBy(
                { it.findAnnotation<Command>()?.commandType != CommandType.PATH },
                { it.name }
            )).forEach { member ->
            result.append(member.findAnnotation<Command>()?.name  + "   ")
        }

        return result.toString().trim()
    }
}

