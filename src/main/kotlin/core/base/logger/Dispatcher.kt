package com.microtik.core.base.logger

interface Dispatcher {
    fun dispatch(message: Message)

    fun setLogger(logger: Logger)

    fun getLogger(): Logger

    fun registerTarget(target: Target)
}