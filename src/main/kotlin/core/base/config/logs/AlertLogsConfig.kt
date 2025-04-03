package com.microtik.core.base.config.logs

import com.google.gson.annotations.SerializedName
import com.microtik.core.base.config.AbstractConfig

data class AlertLogsConfig(
    @SerializedName("path")
    val path: String = "logs/",
    @SerializedName("filename")
    val filename: String = "alert.log"
) : AbstractConfig()
