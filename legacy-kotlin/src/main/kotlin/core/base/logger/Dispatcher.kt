package com.miska.core.base.logger

interface Dispatcher {
    fun dispatch(message: Message)

    fun setLogger(block: () -> Logger)

    fun getLogger(): Logger

    fun registerTarget(block: () -> Target)
}

fun <T : Dispatcher> T.configure(block: T.() -> Unit) {
    block()
}