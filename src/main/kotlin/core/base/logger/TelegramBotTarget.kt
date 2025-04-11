package com.miska.core.base.logger

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.text
import com.miska.Miska
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class TelegramBotTarget(override val levels: List<String>, override val categories: List<String>) : Target {
    private var bot: Bot? = null
    private var scope: CoroutineScope? = null
    private var messageChannel: Channel<String>? = null
    private val messageInterval: Long
    private val chatId: Long?
    private val token: String

    init {
        val config = Miska.app.getConfig().logsConfig.alertLogsConfig.telegramBotConfig
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
            token = "7939012541:AAGjSl4PseCBRujmjg--BfLTco5LduYnTN0"

            dispatch {
                text {
                    bot.sendMessage(message.chat.id, "Идентификатор чата: ${message.chat.id}")
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
                bot!!.sendMessage(chatId!!, message)

                delay(messageInterval)
            }
        }
    }
}

