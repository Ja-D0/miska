package com.miska.core.base.config.logs

import com.google.gson.annotations.SerializedName

data class AlertLogsConfig(
    override val enable: Boolean = false,
    override val path: String = "logs/",
    override val filename: String = "ips-alert.log",
    override val levels: List<String> = listOf("alert"),
    override val categories: List<String> = listOf("ips-alert"),
    @SerializedName("telegram_bot")
    val telegramBotConfig: TelegramBotConfig = TelegramBotConfig()
) : AbstractLogsConfig()
