package com.microtik.core.base.config.microtik

import com.google.gson.annotations.SerializedName
import com.microtik.core.base.config.AbstractConfig

data class MicrotikApiConfig(
    @SerializedName("server")
    val microtikServerConfig: MicrotikServerConfig = MicrotikServerConfig()
) : AbstractConfig() {

    override fun toString(): String {
        return super.toString()
    }
}
