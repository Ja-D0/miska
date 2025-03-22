package com.microtik.core.base.cli.interfaces

/**
 * Интерфейс для реализации ответа на запрос выполнения команды
 *
 * @author Виктория Яковлева
 * @since 0.0.1
 */
interface Response {

    /**
     * Результат выполнения команды, который должен быть отправлен
     *
     * @author Виктория Яковлева
     * @since 0.0.1
     */
    var data: String?

    /**
     * Отправляет результат выполнения команды
     *
     * @author Виктория Яковлева
     * @since 0.0.1
     */
    fun send()
}