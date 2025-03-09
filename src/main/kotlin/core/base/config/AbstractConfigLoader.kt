package com.microtik.core.base.config

abstract class AbstractConfigLoader {
    abstract fun load(configFilePath: String?): AbstractConfig
}
