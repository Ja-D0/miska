package com.miska.core.base.logger

interface Logger {
    fun log(message: String, level: String, category: String = "*")
    fun setDispatcher(dispatcher: Dispatcher)
}