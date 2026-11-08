package com.miska.core.base.interfaces

import com.miska.core.base.cli.CommandsListImpl
import com.miska.core.base.cli.exceptions.CommandsListNotFoundException
import com.miska.core.base.cli.interfaces.CommandsList
import com.miska.core.base.cli.interfaces.Request
import com.miska.core.base.cli.interfaces.Response


/**
 * Интерфейс для реализации приложения
 *
 * @author Денис Чемерис
 * @since 0.0.1
 */
interface Application {

    /**
     * Запускает приложение
     *
     * @author Денис Чемерис
     * @since 0.0.1
     */
    fun run(): Unit

    /**
     * Обрабатывает запрос команды пользователя
     *
     * @return экземпляр [Response]
     * @author Денис Чемерис
     * @since 0.0.1
     */
    fun handleCommandRequest(request: Request): Response

    /**
     * Запускает выполнение команды.
     *
     * @return экземпляр ответа [Response], содержащий ответ команды
     * @author Денис Чемерис
     * @since 0.0.1
     */
    fun runCommand(command: String, params: ArrayList<String>): Response

    /**
     * Возвращает экземпляр текущего каталога
     *
     * @return экземпляр [CommandsListImpl]
     * @throws [CommandsListNotFoundException], если текущий каталог пользователя не найден
     * @author Денис Чемерис
     * @since 0.0.1
     */

    fun getCurrentCommandsList(): CommandsList

    /**
     * Переходит в каталог экземпляра [newCommandsList]
     *
     * @return [Unit]
     * @author Денис Чемерис
     * @since 0.0.1
     */
    fun goToCommandsList(newCommandsList: CommandsList): Boolean

    /**
     * Переходит на каталог назад.
     *
     * @return [Unit]
     * @author Денис Чемерис
     * @since 0.0.1
     */
    fun goBack()


    /**
     * Выводит данные и ожидает ввод данных
     *
     * @author Денис Чемерис
     * @since 0.0.1
     */
    fun cliIn(message: String? = null): String?


    /**
     * Выводит данные
     *
     * @author Денис Чемерис
     * @since 0.0.1
     */
    fun cliOut(message: String)

    /**
     * Запускает приложения для выполнения команд пользователя
     *
     * @see [stop]
     * @author Денис Чемерис
     * @since 0.0.1
     */
    fun start()

    /**
     * Останавливает приложение
     *
     * @see [start]
     * @author Денис Чемерис
     * @since 0.0.1
     */
    fun stop()
}
