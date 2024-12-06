package com.microtik.core.config.configLoader

import com.microtik.core.config.AbstractConfig

abstract class AbstractConfigLoader {
    abstract fun load(configFilePath: String?): AbstractConfig
}
