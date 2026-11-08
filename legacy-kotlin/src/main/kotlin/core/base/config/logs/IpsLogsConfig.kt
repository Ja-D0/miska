package com.miska.core.base.config.logs

data class IpsLogsConfig(
    override val enable: Boolean = false,
    override val path: String = "logs/",
    override val filename: String = "ips.log",
    override val levels: List<String> = listOf("info", "alert"),
    override val categories: List<String> = listOf("ips-info", "ips-alert")
) : AbstractLogsConfig()
