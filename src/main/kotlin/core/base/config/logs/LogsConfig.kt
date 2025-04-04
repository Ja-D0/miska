package com.miska.core.base.config.logs

import com.google.gson.annotations.SerializedName
import com.miska.core.base.config.AbstractConfig

data class LogsConfig(
    @SerializedName("http")
    val httpLogsConfig: HttpLogsConfig = HttpLogsConfig(),
    @SerializedName("app")
    val appLogsConfig: AppLogsConfig = AppLogsConfig(),
    @SerializedName("alert")
    val alertLogsConfig: AlertLogsConfig = AlertLogsConfig()
) : AbstractConfig() {

    override fun toString(): String {
        return super.toString()
    }
}
