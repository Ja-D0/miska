package com.miska.core.base.logger

import com.miska.Miska

class DispatcherImpl : Dispatcher {
    private var logger: Logger? = null
    private var targets: MutableList<Target> = mutableListOf()

    override fun dispatch(message: Message) {
        for (target in targets) {
            target.collect(message)
        }
    }

    override fun setLogger(block: () -> Logger) {
        logger = block()
        logger!!.setDispatcher(this)
    }

    override fun getLogger(): Logger = logger ?: Miska.logger

    override fun registerTarget(block: () -> Target) {
        targets.add(block())
    }
}