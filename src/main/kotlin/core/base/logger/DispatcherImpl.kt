package com.microtik.core.base.logger

import com.microtik.Microtik

class DispatcherImpl : Dispatcher {
    private var logger: Logger? = null
    private var targets: MutableList<Target> = mutableListOf()

    override fun dispatch(message: Message) {
        for (target in targets) {
            target.collect(message)
        }
    }

    override fun setLogger(logger: Logger) {
        this.logger = logger.apply { setDispatcher(this@DispatcherImpl) }
    }

    override fun getLogger(): Logger = logger ?: Microtik.logger

    override fun registerTarget(target: Target) {
        targets.add(target)
    }
}