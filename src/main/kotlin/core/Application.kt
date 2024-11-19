package com.microtik.core

import com.microtik.core.application.AbstractApplication

class Application() : AbstractApplication() {
    override fun run(): Unit
    {
        start()

        while (isRunning) {
            try {
                processCommand(cliManager.cliIn(null))
            } catch (exception: RuntimeException) {
                cliManager.cliOut(exception.message.toString())
            }
        }
    }

    override fun processCommand(command: String?): Unit
    {
        if (command.isNullOrBlank()) return

        val result = cliManager.executeCommand(command)

        if (result is String) {
            return cliManager.cliOut(result)
        }
    }
}