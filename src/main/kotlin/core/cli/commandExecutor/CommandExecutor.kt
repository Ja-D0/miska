package com.microtik.core.cli.commandExecutor

import com.microtik.core.cli.annotations.Command
import com.microtik.core.cli.annotations.CommandType
import com.microtik.core.cli.annotations.Option
import com.microtik.core.cli.commands.AbstractCommands
import com.microtik.core.cli.exceptions.*
import org.apache.commons.cli.*
import kotlin.collections.HashMap
import org.apache.commons.cli.Option as ApacheOption
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.KType
import kotlin.reflect.full.createType
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.functions
import kotlin.reflect.full.isSubtypeOf

//TODO: Переписать и сделать слушатель на -h
open class CommandExecutor<T>(private val commandType: CommandType, private val helpCallback: (String, String, Options) -> Unit) {
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
            val argumentHelper: ArgumentHelper = ArgumentHelper(command, func, cliOptions)
            argumentHelper.extractFunctionOptions()

            return func.callBy(argumentHelper.resolve(commands))
        }

        return func.call(commands)
    }

    private inner class ArgumentHelper(val command: String, val function: KFunction<T>, val cliOptions: List<String>) {

        private lateinit var commandLine: CommandLine
        private val options: Options = Options()

        fun resolve(commands: AbstractCommands): HashMap<KParameter, Any>
        {
            val linkedParams: HashMap<KParameter, Any> = hashMapOf<KParameter, Any>()

            if (validate()) {
                function.parameters.forEach() {
                    val parameterAnnotation = it.findAnnotation<Option>()

                    if (parameterAnnotation !== null && commandLine.hasOption(parameterAnnotation.shortName)) {
                        val value = commandLine.getOptionValue(parameterAnnotation.shortName)
                        println("value: $value, name: ${parameterAnnotation.shortName}")
                        if (value !== null) {
                            linkedParams[it] = resolveParameterType(value, it.type)
                        }
                    }
                }

                return linkedParams.apply { this[function.parameters[0]] = commands }
            }

            throw ValidationErrorException()
        }

        private fun validate(): Boolean = try {
                commandLine = DefaultParser().parse(options, cliOptions.toTypedArray())
                true
            } catch (missingOptionException: MissingOptionException) {
                helpCallback(
                    command,
                    "Отсутствуют обязательные параметры: ${missingOptionException.missingOptions}",
                    options
                )
                false
            } catch (missingArgumentException: MissingArgumentException) {
                helpCallback(
                    command,
                    "Не указано обязательное значение у параметра: -${missingArgumentException.option.key}",
                    options
                )
                false
            } catch (unrecognizedOptionException: UnrecognizedOptionException) {
                helpCallback(
                    command,
                    "Указан неизвестный параметр: ${unrecognizedOptionException.option}",
                    options
                )
                false
            }

        private fun resolveParameterType(value: String, type: KType): Any
        {
            var needType: String = "String"

            val result = when {
                type.isSubtypeOf(Boolean::class.createType()) -> {
                    needType = "Boolean"
                    value.toBoolean()
                }
                type.isSubtypeOf(Int::class.createType()) -> {
                    needType = "Integer"
                    value.toIntOrNull()
                }
                type.isSubtypeOf(Double::class.createType()) -> {
                    needType = "Double"
                    value.toDoubleOrNull()
                }
                type.isSubtypeOf(Long::class.createType()) -> {
                    needType = "Long"
                    value.toLongOrNull()
                }
                type.isSubtypeOf(Float::class.createType()) -> {
                    needType = "Float"
                    value.toFloatOrNull()
                }
                type.isSubtypeOf(String::class.createType()) -> {
                    needType = "String"
                    value
                }
                else -> null
            }

            if (result !== null) {
                return result
            }

            throw ConvertParameterException("Не удалось преобразовать $value к нужному типу: $needType")
        }

        fun extractFunctionOptions(): Unit
        {
            val funcParams = function.parameters.drop(1).filter { kParameter ->
                val optionAnnotation = kParameter.findAnnotation<Option>()

                if (optionAnnotation !== null) {
                    true
                } else if (!kParameter.isOptional) {
                    throw OptionAnnotationNotFoundException("Не указано описании опции для обязательного параметра")
                } else {
                    false
                }
            }

            for (funcParam in funcParams) {
                val optionAnnotation = funcParam.findAnnotation<Option>()
                val option = ApacheOption(
                    optionAnnotation!!.shortName,
                    optionAnnotation.longName,
                    true,
                    optionAnnotation.description
                )

                option.apply { isRequired = optionAnnotation.required }
                options.addOption(option)
            }

            if (commandType === CommandType.COMMAND) {
                options.addOption("h", "help", false, "Показать справку")
            }
        }
    }
}