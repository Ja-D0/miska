package com.miska.core.base.logger

interface Logger {
    fun log(message: String, level: String)
    fun setDispatcher(dispatcher: Dispatcher)
}