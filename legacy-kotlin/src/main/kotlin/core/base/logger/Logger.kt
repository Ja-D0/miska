package com.miska.core.base.logger

interface Logger {
    var dispatcher: Dispatcher

    fun log(message: String, level: String, category: String = "*")
}