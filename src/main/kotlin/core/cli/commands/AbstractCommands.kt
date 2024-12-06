package com.microtik.core.cli.commands

import com.microtik.core.api.endpoints.Api
import com.microtik.core.api.exceptions.FailedRequest
import com.microtik.core.cli.annotations.Command
import com.microtik.core.cli.annotations.CommandType
import retrofit2.Response
import kotlin.reflect.full.declaredFunctions
import kotlin.reflect.full.findAnnotation

abstract class AbstractCommands {
    abstract val path: String
    abstract val apiService: Api?

    @Command("help", CommandType.COMMAND, "Выводит команды текущего каталога")
    fun commandHelp(): String {
        val result: StringBuilder = StringBuilder()

        this::class.declaredFunctions
            .sortedWith(compareBy(
                { it.findAnnotation<Command>()?.commandType != CommandType.PATH },
                { it.name }
            )).forEach { member ->
                result.append(member.findAnnotation<Command>()?.name + "   ")
            }

        return result.toString().trim()
    }

    protected fun <T> runRequest(callable: () -> Response<T>): String {
        val result: String

        val response = callable()

        if (response.isSuccessful && response.body() != null) {
            result = if (response.body() is List<*>) {
                (response.body()!! as List<*>).joinToString("\n") { it.toString() }
            } else {
                response.body()!!.toString()
            }
        } else {
            throw FailedRequest(response.code(), response.body().toString(), response.message())
        }

        return result
    }
}
