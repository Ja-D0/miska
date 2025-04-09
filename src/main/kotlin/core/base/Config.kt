package com.miska.core.base

import com.google.gson.annotations.SerializedName
import com.miska.core.base.config.AbstractConfig
import com.miska.core.base.config.SuricataIpsConfig
import com.miska.core.base.config.logs.LogsConfig
import com.miska.core.base.config.mikrotik.MikrotikApiConfig

data class Config(
    @SerializedName("mikrotik")
    val mikrotikApiConfig: MikrotikApiConfig = MikrotikApiConfig(),
    @SerializedName("suricata_ips")
    val suricataIps: SuricataIpsConfig = SuricataIpsConfig(),
    @SerializedName("logs")
    val logsConfig: LogsConfig = LogsConfig(),
) : AbstractConfig() {

    override fun toString(): String {
        return super.toString()
    }
}
