package com.microtik.core.config.application

import com.microtik.core.config.AbstractConfig
import com.microtik.core.config.logs.LogsConfig
import com.microtik.core.config.microtik.MicrotikApiConfig

data class ApplicationConfig(
     val microtikApiConfig: MicrotikApiConfig,
     val logsConfig: LogsConfig
): AbstractConfig()