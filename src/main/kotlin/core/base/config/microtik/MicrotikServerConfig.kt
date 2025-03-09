package com.microtik.core.base.config.microtik

import com.microtik.core.base.config.AbstractConfig

data class MicrotikServerConfig(
    val host: String = "localhost",
    val port: Int = 80,
    val login: String = "admin",
    val password: String = ""
) : AbstractConfig() {

    override fun toString(): String {
        return super.toString()
    }
}
