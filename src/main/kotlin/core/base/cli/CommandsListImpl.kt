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
import kotlin.reflect.KParameter
import kotlin.reflect.KVisibility
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberFunctions
import com.microtik.core.base.cli.interfaces.Command as CliCommand

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
     * Выполняет по указанному id
     *
     * @param [id] идентификатор команды
     * @return [Any]? результат выполнения команды
     * @author Денис Чемерис
     * @since 0.0.1
     */
    override fun runCommand(id: String): Any? {
        return try {
            createCommand(id).runCommand()
        } catch (validate: ValidationErrorException) {
            Microtik.app.cliOut(validate.message)
            Microtik.app.runCommand("help", arrayListOf("--id", id))
        }
    }

    override fun extractCommandOptions(id: String): List<KParameter> {
        if (!commandIsCommand(id)) {
            throw NotFoundCommandException("Command not found: $id")
        }

        return createCommand(id).extractOptions()
    }

    override fun extractCommandOptions(command: CliCommand): List<KParameter> {
        if (!commandIsCommand(command.id)) {
            throw NotFoundCommandException("Command not found: ${command.id}")
        }

        return command.extractOptions()
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
    override fun createCommand(id: String): CliCommand {
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
     * Проверяет, является ли команда с идентификатором [id] новым каталогом
     *
     * @param [id] id команды для проверки
     * @return [Boolean], true, если команда является каталогом, иначе false
     * @throws [PathConflictException] если реализаций команды [id] более одной
     * @author Денис Чемерис
     * @since 0.0.1
     */
    override fun commandIsPath(id: String): Boolean {
        return try {
            this::class.memberFunctions.single { function ->
                val commandAnnotation = function.findAnnotation<Command>()
                commandAnnotation != null
                        && commandAnnotation.commandType == CommandType.PATH
                        && commandAnnotation.id == id
            }
            true
        } catch (illegalArgumentException: IllegalArgumentException) {
            throw PathConflictException("It is impossible to determine the right path: $id")
        } catch (notFoundException: NoSuchElementException) {
            false
        }
    }

    override fun commandIsCommand(id: String): Boolean {
        return try {
            this::class.memberFunctions.single { function ->
                val commandAnnotation = function.findAnnotation<Command>()
                commandAnnotation != null
                        && commandAnnotation.commandType == CommandType.COMMAND
                        && commandAnnotation.id == id
            }
            true
        } catch (illegalArgumentException: IllegalArgumentException) {
            throw PathConflictException("It is impossible to determine the right path: $id")
        } catch (notFoundException: NoSuchElementException) {
            false
        }
    }

    override fun hasCommand(id: String): Boolean = try {
        this::class.memberFunctions.single { function ->
            val commandAnnotation = function.findAnnotation<Command>()
            commandAnnotation != null
                    && (commandAnnotation.commandType == CommandType.COMMAND || commandAnnotation.commandType == CommandType.PATH)
                    && commandAnnotation.id == id
        }
        true
    } catch (illegalArgumentException: IllegalArgumentException) {
        throw PathConflictException("It is impossible to determine the right path: $id")
    } catch (notFoundException: NoSuchElementException) {
        false
    }
}
