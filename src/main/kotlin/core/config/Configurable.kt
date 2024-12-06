package com.microtik.core.config

interface Configurable {
    fun loadConfig(configFilePath: String?): Configurable
}
