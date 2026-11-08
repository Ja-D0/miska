package com.miska.core.base.config.logs

import com.miska.core.base.config.AbstractConfig

abstract class AbstractLogsConfig : AbstractConfig() {
    abstract val enable: Boolean
    abstract val path: String
    abstract val filename: String
    abstract val levels: List<String>
    abstract val categories: List<String>
}
