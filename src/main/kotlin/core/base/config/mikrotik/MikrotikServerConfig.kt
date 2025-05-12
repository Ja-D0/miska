package com.miska.core.base.config.mikrotik

import com.miska.core.base.config.AbstractConfig

data class MikrotikServerConfig(
    val host: String = "localhost",
    val port: Int = 80,
    val login: String = "admin",
    val password: String = ""
) : AbstractConfig()
