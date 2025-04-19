package com.miska.core.server


import com.miska.Miska
import com.miska.core.base.suricata.SuricataLogAnalyzer
import io.ktor.http.*
import io.ktor.serialization.gson.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

class KtorServer {
    private var server: ApplicationEngine? = null
    private var isRunning = false
    private val suricataLogAnalyzer = SuricataLogAnalyzer()

    fun start(): Boolean {
        if (isRunning) {
            return false
        }

        isRunning = true

        server = embeddedServer(Netty, port = 8080) {
            install(ContentNegotiation) {
                gson()
            }
            routing {
                post("/analyze") {
                    try {
                        var a = call.receive<AlertRequest>()
                        suricataLogAnalyzer.analysis(a)
                        call.respond(HttpStatusCode.OK, a)
                    } catch (e: Exception) {
                        Miska.error(e.message ?: "analysis unknown error")
                        call.respond(HttpStatusCode.InternalServerError, e.message ?: "analysis unknown error")
                    }
                }
            }
        }

        try {
            server?.start()
        } catch (e: Exception) {
            Miska.error("Error when starting a server: ${e.message}")
            isRunning = false
        }

        return true
    }

    fun stop(): Boolean {
        if (!isRunning) {
            return false
        }

        server?.stop(gracePeriodMillis = 1000, timeoutMillis = 3000)
        isRunning = false

        return true
    }
}