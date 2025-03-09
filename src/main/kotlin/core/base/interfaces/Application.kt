package com.microtik.core.base.interfaces


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
