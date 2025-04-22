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
    private var ktor: ApplicationEngine? = null
    private var isRunning = false
    private val suricataLogAnalyzer = SuricataLogAnalyzer()

    fun start(): Boolean {
        if (isRunning) {
            return false
        }

        ktor = embeddedServer(Netty, port = 8080) {
            install(ContentNegotiation) {
                gson()
            }
            routing {
                post("/analyze") {
                    try {
                        suricataLogAnalyzer.analyzeAndMakeADecision(call.receive<AlertRequest>())
                        call.respond(HttpStatusCode.OK, "OK")
                    } catch (e: Exception) {
                        Miska.error(e.message ?: "analysis unknown error")
                        call.respond(HttpStatusCode.InternalServerError, e.message ?: "analysis unknown error")
                    }
                }
            }
        }

        try {
            ktor!!.start()
            isRunning = true

            Miska.info("REST API Server is successfully launched")
        } catch (e: Exception) {
            Miska.error("Error when starting a server: ${e.message}")
            isRunning = false
        }

        return isRunning
    }

    fun stop(): Boolean {
        if (!isRunning) {
            return false
        }

        ktor?.stop(gracePeriodMillis = 1000, timeoutMillis = 3000)
        isRunning = false

        Miska.info("REST API Server is successfully stopped")

        return true
    }

    fun isRunning(): Boolean = isRunning
}