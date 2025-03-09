package com.microtik.core.base.cli

import com.microtik.core.base.cli.annotations.CommandOption
import com.microtik.core.base.cli.exceptions.CommandOptionAnnotationNotFoundException
import com.microtik.core.base.cli.exceptions.ConvertParameterException
import com.microtik.core.base.cli.exceptions.ValidationErrorException
import com.microtik.core.base.cli.interfaces.Command
import org.apache.commons.cli.*
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.KType
import kotlin.reflect.full.createType
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.isSubtypeOf

/**
 *
 */
abstract class CommandImpl(
    val id: String,
    private val owner: CommandsListImpl,
    private val command: KFunction<Any?>
) : Command {

    override fun runCommand(options: ArrayList<String>): Any? {
        val args = bindCommandOptions(extractOptions(), options)

        return command.callBy(args)
    }

    /**
     *
     */
    fun bindCommandOptions(options: Options, params: ArrayList<String>): Map<KParameter, Any> {
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

    fun resolveParameterType(value: String, type: KType): Any {
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
     *
     */
    fun parseParams(options: Options, params: ArrayList<String>): CommandLine {
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
     *
     */
    fun extractOptions(): Options {
        val options = Options()

        command.parameters.drop(1).forEach { parameter ->
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
