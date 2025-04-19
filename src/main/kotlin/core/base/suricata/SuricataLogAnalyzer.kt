package com.miska.core.base.suricata

import com.miska.Miska
import com.miska.core.base.logger.TelegramBotTarget
import com.miska.core.server.AlertRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap

class SuricataLogAnalyzer {
    private val scope = CoroutineScope(Dispatchers.IO)
    private val processingAddresses = ConcurrentHashMap<String, String>()
    private val suricataFirewallManager = SuricataFirewallManager()

    init {
        registerTelegramBotAlerts()
    }

    suspend fun analysis(alert: AlertRequest) {
        if (processingAddresses.putIfAbsent(alert.srcIp, alert.srcIp) != null) {
            Miska.info("${alert.srcIp} is already being processed.", "suricata-info")

            return
        }

        scope.launch {

            val srcIpAddress = alert.srcIp

            // Анализируем ...

            processingAddresses.remove(alert.srcIp)
        }
    }

    private fun registerTelegramBotAlerts() {
        Miska.logger.dispatcher.registerTarget {
            TelegramBotTarget(
                listOf("alert"),
                listOf("suricata-alert")
            )
        }
    }
}