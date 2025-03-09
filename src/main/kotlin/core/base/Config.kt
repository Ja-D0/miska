package com.microtik.core.base

import com.google.gson.annotations.SerializedName
import com.microtik.core.base.config.AbstractConfig
import com.microtik.core.base.config.logs.LogsConfig
import com.microtik.core.base.config.microtik.MicrotikApiConfig

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
