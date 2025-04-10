package com.miska.core.base.logger

import java.text.SimpleDateFormat
import java.util.*

class LoggerImpl : Logger {
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    private var dispatcher: Dispatcher = DispatcherImpl()

    override fun log(message: String, level: String, category: String) =
        dispatcher.dispatch(Message(message, level, category, dateFormat.format(Date())))

    override fun setDispatcher(dispatcher: Dispatcher) {
        this.dispatcher = dispatcher
    }
}