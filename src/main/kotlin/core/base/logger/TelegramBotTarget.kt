package com.miska.core.base.logger

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.text
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.ParseMode
import com.miska.core.base.config.logs.TelegramBotConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class TelegramBotTarget(
    config: TelegramBotConfig,
    override val levels: List<String>,
    override val categories: List<String>
) : Target {
    private var bot: Bot? = null
    private var scope: CoroutineScope? = null
    private var messageChannel: Channel<String>? = null
    private val messageInterval: Long
    private val chatId: Long?
    private val token: String

    init {
        messageInterval = config.messageInterval
        chatId = config.chatId
        token = config.token

        if (token.isNotEmpty()) {
            initBot()

            if (chatId != null) {
                startMessageSender()
            }
        }
    }

    override fun collect(message: Message) {
        if (token.isNotEmpty() && chatId != null) {
            messageChannel!!.trySend(message.message)
        }
    }

    private fun initBot() {
        bot = bot {
            token = this@TelegramBotTarget.token

            dispatch {
                text {
                    bot.sendMessage(ChatId.fromId(message.chat.id), "Идентификатор чата: ${message.chat.id}")
                }
            }
        }

        bot!!.startPolling()
    }

    private fun startMessageSender() {
        messageChannel = Channel(Channel.UNLIMITED)
        scope = CoroutineScope(Dispatchers.IO)

        scope!!.launch {
            messageChannel!!.consumeEach { message ->
                bot!!.sendMessage(ChatId.fromId(chatId!!), message, ParseMode.MARKDOWN_V2)

                delay(messageInterval)
            }
        }
    }
}

