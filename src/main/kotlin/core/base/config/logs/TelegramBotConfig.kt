package com.miska.core.base.config.logs

import com.google.gson.annotations.SerializedName
import com.miska.core.base.config.AbstractConfig

data class TelegramBotConfig(
    @SerializedName("token")
    val token: String = "",
    @SerializedName("chatId")
    val chatId: Long? = null,
    @SerializedName("message_interval")
    val messageInterval: Long = 3000
) : AbstractConfig()
