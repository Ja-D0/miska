package com.miska.core.base.cli

import com.miska.Miska
import com.miska.core.base.cli.interfaces.Response

/**
 * Класс, реализующий ответ на запрос выполнения команды
 *
 * @author Виктория Яковлева
 * @since 0.0.1
 */
class Response : Response {

    /**
     * Результат выполнения команды, который будет выведен пользователю
     *
     * @author Виктория Яковлева
     * @since 0.0.1
     */
    override var data: String? = null


    /**
     * Отправляет результат выполнения команды
     *
     * @author Виктория Яковлева
     * @since 0.0.1
     */
    override fun send() {
        if (data != null) {
            Miska.app.cliOut(data!!)
        }
    }
}