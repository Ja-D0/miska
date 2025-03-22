package com.microtik.core.base.cli.interfaces

import kotlin.reflect.KParameter

/**
 * Интерфейс для реализации поведения списка команд и их запуска
 *
 * @author Денис Чемерис
 * @since 0.0.1
 */
interface CommandsList {

    /**
     * Создает экземпляр команды по ее идентификатору [id]
     *
     * @param [id] id команды, которую необходимо создать
     * @return экземпляр [Command]
     * @author Денис Чемерис
     * @since 0.0.1
     */
    fun createCommand(id: String): Command

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

    /**
     * Выполняет команду по указанному id
     *
     * @param [id] идентификатор команды
     * @return [Any]? результат выполнения команды
     * @author Денис Чемерис
     * @since 0.0.1
     */
    fun runCommand(id: String): Any?

    fun extractCommandOptions(id: String): List<KParameter>

    fun extractCommandOptions(command: Command): List<KParameter>

    fun hasCommand(id: String): Boolean

    fun commandIsPath(id: String): Boolean

    fun commandIsCommand(id: String): Boolean
}