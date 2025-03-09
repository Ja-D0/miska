package com.microtik.core.base.cli.interfaces

/**
 * Интерфейс для реализации поведения списка команд и их запуска
 *
 * @author Денис Чемерис
 * @since 0.0.1
 */
interface CommandsList {

    /**
     * Выполняет команду с параметрами по указанному id
     *
     * @param [id] идентификатор команды
     * @param [params] массив с параметрами для функции команды
     * @return [Any]? результат выполнения команды
     * @author Денис Чемерис
     * @since 0.0.1
     */
    fun runCommand(id: String, params: ArrayList<String>): Any?
}