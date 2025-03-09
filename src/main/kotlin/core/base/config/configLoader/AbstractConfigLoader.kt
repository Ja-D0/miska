package com.microtik.core.base.config.configLoader

import com.microtik.core.base.config.AbstractConfig

abstract class AbstractConfigLoader {
    abstract fun load(configFilePath: String?): AbstractConfig
}
