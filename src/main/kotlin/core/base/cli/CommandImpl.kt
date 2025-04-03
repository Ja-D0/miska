package com.microtik.core.base.cli

import com.microtik.Microtik
import com.microtik.core.base.cli.annotations.CommandOption
import com.microtik.core.base.cli.exceptions.CommandOptionAnnotationNotFoundException
import com.microtik.core.base.cli.exceptions.ConvertParameterException
import com.microtik.core.base.cli.exceptions.ValidationErrorException
import com.microtik.core.base.cli.interfaces.Command
import org.apache.commons.cli.*
import java.lang.reflect.InvocationTargetException
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.KType
import kotlin.reflect.full.createType
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.isSubtypeOf

/**
 * Базовый класс, реализующий поведение команды
 *
 * @author Виктория Яковлева
 * @since 0.0.1
 */
abstract class CommandImpl(
    /**
     * Идентификатор команды
     *
     * @author Виктория Яковлева
     * @since 0.0.1
     */
    override val id: String,
    private val owner: CommandsListImpl,
    private val command: KFunction<Any?>
) : Command {

    /**
     * Запускает выполнение команды с указанными параметрами
     *
     * @param [options] массив с параметрами, которые будут переданы команде при запуске
     * @return [Any]? результат выполнения команды
     * @author Виктория Яковлева
     * @since 0.0.1
     */
    override fun runCommand(options: ArrayList<String>): Any? {
        val args = bindCommandOptions(extractOptions(), options)

        return try {
            Microtik.info("Run command $id")

            command.callBy(args)
        } catch (invocationTargetException: InvocationTargetException) {
            throw invocationTargetException.cause!!
        }
    }


    /**
     * Запускает выполнение команды без параметров
     *
     * @return [Any]? результат выполнения команды
     * @author Виктория Яковлева
     * @since 0.0.1
     */
    override fun runCommand(): Any? {
        return try {
            command.call(owner)
        } catch (invocationTargetException: InvocationTargetException) {
            throw invocationTargetException.cause!!
        }
    }

    /**
     * Извлекает параметры команды на основе указанных [CommandOption]
     *
     * @return экземпляр [List]
     * @throws CommandOptionAnnotationNotFoundException если у обязательного аргумента команды не указана
     * аннотация [CommandOption]
     * @author Виктория Яковлева
     * @since 0.0.1
     */
    override fun extractOptions(): List<KParameter> {
        val options = mutableListOf<KParameter>()

        command.parameters.drop(1).forEach { parameter ->
            val annotation = parameter.findAnnotation<CommandOption>()
            if (annotation != null) {
                options.add(parameter)
            } else if (!parameter.isOptional) {
                throw CommandOptionAnnotationNotFoundException(
                    "Missing ${CommandOption::class.qualifiedName} for required parameter: \"${parameter.name}\" " +
                            "in ${owner::class.qualifiedName}::${command.name}"
                )
            }
        }

        return options
    }

    /**
     * Извлекает параметры команды на основе указанных [CommandOption]
     *
     * @return экземпляр [Options]
     * @throws CommandOptionAnnotationNotFoundException если у обязательного аргумента команды не указана
     * аннотация [CommandOption]
     * @author Виктория Яковлева
     * @since 0.0.1
     */
    override fun extractOptionsAsOptions(): Options = listOptionsAsOptions(extractOptions())

    /**
     * Соотносит массив параметров команды с соответствующими аргументами функции реализации команды
     *
     * @param [listOptions] экземпляр [List], содержащий данные об параметрах команды на основе указанных [CommandOption]
     * @param [params] массив с параметрами
     * @return [Map] массив соотнесенных аргументов команды
     * @author Виктория Яковлева
     * @since 0.0.1
     */
    private fun bindCommandOptions(listOptions: List<KParameter>, params: ArrayList<String>): Map<KParameter, Any> {
        val options = listOptionsAsOptions(listOptions)

        val linkedParams = mutableMapOf(Pair<KParameter, Any>(command.parameters[0], owner))

        val commandLine = parseParams(options, params)

        command.parameters.forEach { parameter ->
            val annotation = parameter.findAnnotation<CommandOption>()
            if (annotation != null && commandLine.hasOption(annotation.shortName)) {
                val value = commandLine.getOptionValue(annotation.shortName)

                value?.let {
                    linkedParams[parameter] = resolveParameterType(it, parameter.type)
                }
            }
        }

        return linkedParams
    }

    /**
     * Разрешает тип значения параметра на основе типа аргумента команды
     *
     * @param [value] значение параметра
     * @param [type] тип необходимый для аргумента команды
     * @return [Any] результат преобразования
     * @throws [ConvertParameterException] если значение параметра [value] невозможно преобразовать к типу [type]
     * @author Виктория Яковлева
     * @since 0.0.1
     */
    private fun resolveParameterType(value: String, type: KType): Any {
        return when {
            type.isSubtypeOf(Boolean::class.createType(nullable = true)) -> value.toBoolean()
            type.isSubtypeOf(Int::class.createType(nullable = true)) -> value.toIntOrNull()
                ?: throw ConvertParameterException("Cannot convert $value to Integer")

            type.isSubtypeOf(Double::class.createType(nullable = true)) -> value.toDoubleOrNull()
                ?: throw ConvertParameterException("Cannot convert $value to Double")

            type.isSubtypeOf(Long::class.createType(nullable = true)) -> value.toLongOrNull()
                ?: throw ConvertParameterException("Cannot convert $value to Long")

            type.isSubtypeOf(Float::class.createType(nullable = true)) -> value.toFloatOrNull()
                ?: throw ConvertParameterException("Cannot convert $value to Float")

            type.isSubtypeOf(String::class.createType(nullable = true)) -> value
            else -> throw ConvertParameterException("Cannot convert $value to type: ${type}")
        }
    }

    /**
     * Парсит параметры [params], введенные пользователем и соотносит их с необходимыми аргументами команды [options]
     *
     * @param [options] экземпляр [Options], содержащий данные об параметрах команды на основе указанных [CommandOption]
     * @param [params] массив с параметрами
     * @throws [ValidationErrorException] если не указан обязательный параметр или не указано значение для
     * обязательного параметра или указан неизвестный параметр
     * @author Виктория Яковлева
     * @since 0.0.1
     */
    private fun parseParams(options: Options, params: ArrayList<String>): CommandLine {
        return try {
            DefaultParser().parse(options, params.toTypedArray())
        } catch (missingOptionException: MissingOptionException) {
            throw ValidationErrorException("Missing required option: ${missingOptionException.missingOptions}")
        } catch (missingArgumentException: MissingArgumentException) {
            throw ValidationErrorException("The required value of the option is not indicated: -${missingArgumentException.option.key}")
        } catch (unrecognizedOptionException: UnrecognizedOptionException) {
            throw ValidationErrorException("The unknown parameter is indicated: ${unrecognizedOptionException.option}")
        }
    }

    /**
     * Преобразует массив [List] с параметрами в экземпляр класс [Options]
     *
     * @return экземпляр [Options]
     * @throws CommandOptionAnnotationNotFoundException если у обязательного аргумента команды не указана
     * аннотация [CommandOption]
     * @author Виктория Яковлева
     * @since 0.0.1
     */
    private fun listOptionsAsOptions(listOptions: List<KParameter>): Options {
        val options = Options()

        listOptions.forEach { parameter ->
            val annotation = parameter.findAnnotation<CommandOption>()
            if (annotation != null) {
                val option = Option(annotation.shortName, annotation.longName, true, annotation.description)
                    .apply {
                        isRequired = annotation.required
                    }
                options.addOption(option)
            } else if (!parameter.isOptional) {
                throw CommandOptionAnnotationNotFoundException(
                    "Missing ${CommandOption::class.qualifiedName} for required parameter: \"${parameter.name}\" " +
                            "in ${owner::class.qualifiedName}::${command.name}"
                )
            }
        }

        return options
    }
}
