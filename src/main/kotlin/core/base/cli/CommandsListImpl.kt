package com.microtik.core.base.cli

import com.microtik.Microtik
import com.microtik.core.base.cli.annotations.Command
import com.microtik.core.base.cli.annotations.CommandType
import com.microtik.core.base.cli.exceptions.CommandConflictException
import com.microtik.core.base.cli.exceptions.NotFoundCommandException
import com.microtik.core.base.cli.exceptions.PathConflictException
import com.microtik.core.base.cli.exceptions.ValidationErrorException
import com.microtik.core.base.cli.interfaces.CommandsList
import org.apache.commons.cli.DefaultParser
import org.apache.commons.cli.Options
import kotlin.reflect.KFunction
import kotlin.reflect.KVisibility
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberFunctions

/**
 * Базовый класс, реализующий поведение списка команд и их запуска
 *
 * @author Денис Чемерис
 * @since 0.0.1
 */
abstract class CommandsListImpl : CommandsList {

    /**
     * Выполняет команду с параметрами по указанному id
     *
     * @param [id] идентификатор команды
     * @param [params] массив с параметрами для функции команды
     * @return [Any]? результат выполнения команды
     * @author Денис Чемерис
     * @since 0.0.1
     */
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
     * Проверяет, является ли текущая команда запросом на вывод помощи по команде
     *
     * @return [Boolean], true, если это запрос вывода справки по команде, иначе false
     * @author Денис Чемерис
     * @since 0.0.1
     */
    private fun isHelpRequest(params: ArrayList<String>): Boolean {
        if (params.isEmpty()) {
            return false
        }

        val helpOptions = Options().apply {
            addOption("h", "help", false, "")
        }

        return DefaultParser().parse(helpOptions, params.toTypedArray(), true).hasOption("h")
    }

    /**
     * Создает экземпляр команды [CommandImpl] по ее идентификатору [id]
     *
     * @param [id] id команды, которую необходимо создать
     * @return экземпляр [CommandImpl]
     * @throws [NotFoundCommandException] если реализация команды [id] не будет найдена или она не является публичной
     * @throws [CommandConflictException] если реализаций команды [id] более одной
     * @author Денис Чемерис
     * @since 0.0.1
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
     * Проверяет, является ли команда с идентификатором [commandId] новым каталогом
     *
     * @param [commandId] id команды для проверки
     * @return [Boolean], true, если команда является каталогом, иначе false
     * @throws [PathConflictException] если реализаций команды [commandId] более одной
     * @author Денис Чемерис
     * @since 0.0.1
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
