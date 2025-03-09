package com.microtik.core.base.config.logs

import com.google.gson.annotations.SerializedName
import com.microtik.core.base.config.AbstractConfig

data class HttpLogsConfig(
    @SerializedName("path")
    val path: String
) : AbstractConfig() {

    override fun toString(): String {
        return super.toString()
    }
}
