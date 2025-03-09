package com.microtik.core.base.config.logs

import com.google.gson.annotations.SerializedName
import com.microtik.core.base.config.AbstractConfig

data class LogsConfig(
    @SerializedName("error")
    val errorConfig: ErrorLogsConfig = ErrorLogsConfig(),
    @SerializedName("http")
    val httpLogsConfig: HttpLogsConfig = HttpLogsConfig()
) : AbstractConfig() {

    override fun toString(): String {
        return super.toString()
    }
}
