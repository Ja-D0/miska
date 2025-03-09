package com.microtik.core.base.cli.interfaces

/**
 * Интерфейс для реализации ответа на запрос выполнения команды
 *
 * @author Виктория Яковлева
 * @since 0.0.1
 */
interface Response {

    /**
     * Отправляет результат выполнения команды
     *
     * @author Виктория Яковлева
     * @since 0.0.1
     */
    fun send()
}