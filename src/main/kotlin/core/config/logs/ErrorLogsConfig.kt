package com.microtik.core.config.logs

import com.google.gson.annotations.SerializedName
import com.microtik.core.config.AbstractConfig

data class ErrorLogsConfig(
    @SerializedName("path")
    val path: String
): AbstractConfig()