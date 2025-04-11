package com.miska.core.base.config.logs

import com.google.gson.annotations.SerializedName
import com.miska.core.base.config.AbstractConfig

data class AlertLogsConfig(
    @SerializedName("path")
    val path: String = "logs/",
    @SerializedName("filename")
    val filename: String = "alert.log",
    @SerializedName("telegram_bot")
    val telegramBotConfig: TelegramBotConfig = TelegramBotConfig()
) : AbstractConfig()
