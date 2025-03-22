package com.microtik.core.base.cli.interfaces

import com.microtik.core.base.cli.annotations.CommandOption
import org.apache.commons.cli.Options
import kotlin.reflect.KParameter

/**
 * Интерфейс для реализации поведения команды
 *
 * @author Виктория Яковлева
 * @since 0.0.1
 */
interface Command {

    val id: String

    /**
     * Запускает выполнение команды с указанными параметрами
     *
     * @param [options] массив с параметрами, которые будут переданы команде при запуске
     * @return [Any]? результат выполнения команды
     * @author Виктория Яковлева
     * @since 0.0.1
     *
     */
    fun runCommand(options: ArrayList<String>): Any?

    /**
     * Запускает выполнение команды без параметров
     *
     * @return [Any]? результат выполнения команды
     * @author Виктория Яковлева
     * @since 0.0.1
     */
    fun runCommand(): Any?

    /**
     * Извлекает параметры команды на основе указанных [CommandOption]
     *
     * @return экземпляр [List]
     * @author Виктория Яковлева
     * @since 0.0.1
     */
    fun extractOptions(): List<KParameter>

    /**
     * Извлекает параметры команды на основе указанных [CommandOption]
     *
     * @return экземпляр [Options]
     * @author Виктория Яковлева
     * @since 0.0.1
     */
    fun extractOptionsAsOptions(): Options
}