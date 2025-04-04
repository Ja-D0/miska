package com.miska.core.base.config.logs

import com.google.gson.annotations.SerializedName
import com.miska.core.base.config.AbstractConfig

data class HttpLogsConfig(
    @SerializedName("path")
    val path: String = "logs/",
    @SerializedName("filename")
    val filename: String = "http.log"
) : AbstractConfig()
