package com.microtik.core.config.logs

import com.google.gson.annotations.SerializedName
import com.microtik.core.config.AbstractConfig

data class LogsConfig(
    @SerializedName("error")
    val errorConfig: ErrorLogsConfig,
    @SerializedName("http")
    val httpLogsConfig: HttpLogsConfig
) : AbstractConfig() {

    override fun toString(): String {
        return super.toString()
    }
}
