package com.microtik.core.base.config.logs

import com.google.gson.annotations.SerializedName
import com.microtik.core.base.config.AbstractConfig

data class ErrorLogsConfig(
    @SerializedName("path")
    val path: String = "logs/"
) : AbstractConfig() {

    override fun toString(): String {
        return super.toString()
    }
}
