package com.microtik.core.config.application

import com.google.gson.annotations.SerializedName
import com.microtik.core.config.AbstractConfig
import com.microtik.core.config.logs.LogsConfig
import com.microtik.core.config.microtik.MicrotikApiConfig

data class Config(
    @SerializedName("microtik_api")
    val microtikApiConfig: MicrotikApiConfig,
    @SerializedName("logs")
    val logsConfig: LogsConfig,
) : AbstractConfig() {

    override fun toString(): String {
        return super.toString()
    }
}
