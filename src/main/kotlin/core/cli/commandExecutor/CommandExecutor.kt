package com.microtik.core.cli.commandExecutor

import com.microtik.core.cli.annotations.Command
import com.microtik.core.cli.annotations.CommandType
import com.microtik.core.cli.annotations.Option
import com.microtik.core.cli.commands.AbstractCommands
import com.microtik.core.cli.exceptions.CommandConflictException
import com.microtik.core.cli.exceptions.NotFoundCommandException
import com.microtik.core.cli.exceptions.ValidationErrorException
import org.apache.commons.cli.*
import org.apache.commons.cli.Option as ApacheOption
import kotlin.reflect.KFunction
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.functions

open class CommandExecutor<T>(private val commandType: CommandType, private val validationError: (String, String, Options) -> Unit) {
    fun execute(command: String, cliOptions: List<String>, commands: AbstractCommands): T {
        val func: KFunction<T>

        try {
            func = commands::class.functions.single { function ->
                val commandAnnotation = function.findAnnotation<Command>()
                commandAnnotation != null
                        && commandAnnotation.commandType == commandType
                        && commandAnnotation.name == command
            } as KFunction<T>
        } catch (illegalArgumentException: IllegalArgumentException) {
            throw CommandConflictException("Невозможно определить команду: $command")
        } catch (notFoundException: NoSuchElementException) {
            throw NotFoundCommandException("Команда не найдена: $command")
        }

        if (commandType == CommandType.COMMAND) {
            val funcOptions = getCommandOptions(func)

            if (validate(command, funcOptions, cliOptions)) {
                return if (cliOptions.isNotEmpty()) func.call(commands, cliOptions) else func.call(commands)
            }

            throw ValidationErrorException()
        }

        return func.call(commands)
    }
    private fun validate(command: String, funcOptions: Options, options: List<String>): Boolean
    {
        return try {
            DefaultParser().parse(funcOptions, options.toTypedArray())
            true
        } catch (missingOptionException: MissingOptionException) {
            validationError(command, "Отсутствуют обязательные параметры: ${missingOptionException.missingOptions}", funcOptions)
            false
        } catch (missingArgumentException: MissingArgumentException) {
            validationError(command, "Не указано обязательное значение у параметра: -${missingArgumentException.option.key}", funcOptions)
            false
        } catch (unrecognizedOptionException: UnrecognizedOptionException) {
            validationError(command, "Указан неизвестный параметр: ${unrecognizedOptionException.option}", funcOptions)
            false
        }
    }

    private fun getCommandOptions(function: KFunction<T>): Options
    {
        val options = Options()
        val funcParams = function.parameters.filter { kParameter ->
            val optionAnnotation = kParameter.findAnnotation<Option>()
            optionAnnotation !== null
        }

        for (funcParam in funcParams) {
            val optionAnnotation = funcParam.findAnnotation<Option>()
            val option = ApacheOption(
                optionAnnotation!!.shortName,
                optionAnnotation.longName,
                optionAnnotation.required,
                optionAnnotation.description
            )

            option.apply { isRequired = true }
            options.addOption(option)
        }

        options.addOption("h", "help", false, "Показать справку")

        return options
    }
}