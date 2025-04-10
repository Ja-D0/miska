package com.miska.core.base.logger

import com.miska.Miska

class DispatcherImpl : Dispatcher {
    private var logger: Logger? = null
    private var targets: MutableList<Target> = mutableListOf()

    override fun dispatch(message: Message) {
        val (neededLevel, neededCategory) = determineTheConditionsForTargets(message)

        val targetsForCollect = targets.filter {
            val levelCondition = it.levels.contains(neededLevel) || it.levels.contains("*")
            val categoryCondition = it.categories.contains(neededCategory) || it.categories.contains("*")

            levelCondition && categoryCondition
        }

        for (target in targetsForCollect) {
            target.collect(message)
        }
    }

    private fun determineTheConditionsForTargets(message: Message): Pair<String, String> {
        var category = "*"
        var level = "*"

        if (message.category != "*") {
            category = message.category
        }

        if (message.level != "*") {
            level = message.level
        }

        return Pair(level, category)
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