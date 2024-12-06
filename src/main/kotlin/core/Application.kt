package com.microtik.core

import com.microtik.core.application.AbstractApplication
import java.lang.Exception

class Application() : AbstractApplication() {
    override fun run(): Unit {
        start()

        while (isRunning) {
            try {
                processCommand(cliManager.cliIn(null))
            } catch (exception: Exception) {
                if (exception.message != null) {
                    cliManager.cliOut(exception.message.toString())
                }
            }
        }
    }

    override fun processCommand(command: String?): Unit {
        if (command.isNullOrBlank()) return

        val result = cliManager.parseCommandLine(command)

        if (result.isNotEmpty()) {
            return cliManager.cliOut(result)
        }
    }
}
