package com.miska.core.base.config.logs

data class HttpLogsConfig(
    override val enable: Boolean = false,
    override val path: String = "logs/",
    override val filename: String = "http.log",
    override val levels: List<String> = listOf("http"),
    override val categories: List<String> = listOf("*")
) : AbstractLogsConfig()
