package com.microtik.core.config.microtik

import com.microtik.core.config.AbstractConfig

data class MicrotikServerConfig(
    val host: String,
    val port: Int,
    val login: String,
    val password: String
): AbstractConfig()