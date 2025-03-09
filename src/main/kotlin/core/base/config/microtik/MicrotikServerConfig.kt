package com.microtik.core.base.config.microtik

import com.microtik.core.base.config.AbstractConfig

data class MicrotikServerConfig(
    val host: String,
    val port: Int,
    val login: String,
    val password: String
) : AbstractConfig() {

    override fun toString(): String {
        return super.toString()
    }
}
