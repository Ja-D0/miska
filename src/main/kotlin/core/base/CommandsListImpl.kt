package com.microtik.core.base

import com.microtik.Microtik
import com.microtik.core.base.cli.annotations.Command
import com.microtik.core.base.cli.annotations.CommandType
import com.microtik.core.base.cli.exceptions.CommandConflictException
import com.microtik.core.base.cli.exceptions.NotFoundCommandException
import com.microtik.core.base.cli.exceptions.PathConflictException
import com.microtik.core.base.cli.exceptions.ValidationErrorException
import com.microtik.core.base.interfaces.CommandsList
import org.apache.commons.cli.DefaultParser
import org.apache.commons.cli.Options
import kotlin.reflect.KFunction
import kotlin.reflect.KVisibility
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberFunctions

/**
 *
 */
abstract class CommandsListImpl : CommandsList {
    override fun runCommand(id: String, params: ArrayList<String>): Any? {
        if (isHelpRequest(params)) {
            return Microtik.app.runCommand("help", arrayListOf("--id", id))
        }

        return try {
            createCommand(id).runCommand(params)
        } catch (validate: ValidationErrorException) {
            Microtik.app.cliOut(validate.message)
            Microtik.app.runCommand("help", arrayListOf("--id", id))
        }
    }

    /**
     *
     */
    fun isHelpRequest(params: ArrayList<String>): Boolean {
        if (params.isEmpty()) {
            return false
        }

        val helpOptions = Options().apply {
            addOption("h", "help", false, "")
        }

        return DefaultParser().parse(helpOptions, params.toTypedArray(), true).hasOption("h")
    }

    /**
     *
     */
    fun createCommand(id: String): CommandImpl {
        val func: KFunction<*>

        try {
            func = this::class.memberFunctions.single { function ->
                val commandAnnotation = function.findAnnotation<Command>()
                commandAnnotation != null && commandAnnotation.id == id
            }
        } catch (illegalArgumentException: IllegalArgumentException) {
            throw CommandConflictException("It is impossible to determine the command: $id")
        } catch (notFoundException: NoSuchElementException) {
            throw NotFoundCommandException("Command not found: $id")
        }

        if (func.visibility == KVisibility.PUBLIC) {
            return InlineCommand(id, this, func)
        } else {
            throw throw NotFoundCommandException("Command not found: $id")
        }
    }

    /**
     * Проверяет, является команда каталогом
     *
     *
     */
    fun isPath(commandId: String): Boolean {
        return try {
            this::class.memberFunctions.single { function ->
                val commandAnnotation = function.findAnnotation<Command>()
                commandAnnotation != null
                        && commandAnnotation.commandType == CommandType.PATH
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
