package com.microtik.core.base.logger

interface Logger {
    fun log(message: String, level: String)
    fun setDispatcher(dispatcher: Dispatcher)
}