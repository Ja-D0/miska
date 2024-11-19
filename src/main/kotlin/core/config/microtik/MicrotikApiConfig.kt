package com.microtik.core.config.microtik

import com.google.gson.annotations.SerializedName
import com.microtik.core.config.AbstractConfig

data class MicrotikApiConfig(
    @SerializedName("microtik_server")
    val microtikServerConfig: MicrotikServerConfig
): AbstractConfig() {

    override fun toString(): String {
        return super.toString()
    }
}
