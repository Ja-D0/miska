package com.miska.core.base.config.mikrotik

import com.google.gson.annotations.SerializedName
import com.miska.core.base.config.AbstractConfig

data class MikrotikApiConfig(
    @SerializedName("server")
    val mikrotikServerConfig: MikrotikServerConfig = MikrotikServerConfig()
) : AbstractConfig() {

    override fun toString(): String {
        return super.toString()
    }
}
