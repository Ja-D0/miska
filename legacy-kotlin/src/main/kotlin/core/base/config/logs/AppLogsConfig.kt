package com.miska.core.base.config.logs

data class AppLogsConfig(
    override val enable: Boolean = true,
    override val path: String = "logs/",
    override val filename: String = "app.log",
    override val levels: List<String> = listOf("*"),
    override val categories: List<String> = listOf("*"),
) : AbstractLogsConfig()
