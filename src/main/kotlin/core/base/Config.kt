package com.microtik.core.base

import com.google.gson.annotations.SerializedName
import com.microtik.core.base.config.AbstractConfig
import com.microtik.core.base.config.logs.LogsConfig
import com.microtik.core.base.config.microtik.MicrotikApiConfig

data class Config(
    @SerializedName("microtik")
    val microtikApiConfig: MicrotikApiConfig = MicrotikApiConfig(),
    @SerializedName("logs")
    val logsConfig: LogsConfig = LogsConfig(),
) : AbstractConfig() {

    override fun toString(): String {
        return super.toString()
    }
}
