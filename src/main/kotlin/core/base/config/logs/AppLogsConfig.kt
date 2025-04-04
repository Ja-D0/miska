package com.miska.core.base.config.logs

import com.google.gson.annotations.SerializedName
import com.miska.core.base.config.AbstractConfig

data class AppLogsConfig(
    @SerializedName("path")
    val path: String = "logs/",
    @SerializedName("filename")
    val filename: String = "app.log"
) : AbstractConfig()
