package com.miska.core.base.config

import com.google.gson.annotations.SerializedName
import com.miska.core.base.config.ips.IpsConfig
import com.miska.core.base.config.logs.LogsConfig
import com.miska.core.base.config.mikrotik.MikrotikApiConfig

data class Config(
    @SerializedName("mikrotik")
    val mikrotikApiConfig: MikrotikApiConfig = MikrotikApiConfig(),
    @SerializedName("ips")
    val ipsConfig: IpsConfig = IpsConfig(),
    @SerializedName("logs")
    val logsConfig: LogsConfig = LogsConfig(),
) : AbstractConfig()


