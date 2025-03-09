package com.microtik.core.base.cli.interfaces

/**
 * Интерфейс для реализации поведения команды
 *
 * @author Виктория Яковлева
 * @since 0.0.1
 */
interface Command {

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
}