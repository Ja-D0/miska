package com.microtik.core

import com.microtik.core.base.ApplicationImpl
import com.microtik.core.base.interfaces.Request
import com.microtik.core.base.interfaces.Response

/**
 *
 */
class Application : ApplicationImpl() {

    /**
     * Обрабатывает запрос команды пользователя
     *
     * @return экземпляр [Response]
     * @author Денис Чемерис
     * @since 0.0.1
     */
    override fun handleCommandRequest(request: Request): Response {
        val (command, params) = request.resolve()

        return runCommand(command, params)
    }
}
