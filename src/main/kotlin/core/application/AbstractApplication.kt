package com.microtik.core.application

import com.microtik.Microtik
import com.microtik.core.cli.CliManager.CliManager
import com.microtik.core.config.application.Config
import com.microtik.core.config.Configurable
import com.microtik.core.config.configLoader.ConfigLoader
import java.io.File
import java.io.FileNotFoundException
import kotlin.system.exitProcess

abstract class AbstractApplication: Application, Configurable {
    protected var isRunning: Boolean = false
    private lateinit var config: Config
    protected val cliManager: CliManager = CliManager()
    private val credentials: Map<String, String> = mapOf("login" to "admin", "password" to "admin")

    abstract override fun run()
    abstract override fun processCommand(command: String?)

    fun getConfig(): Config = config

    override fun loadConfig(configFilePath: String?): AbstractApplication
    {
        try {
            config = ConfigLoader().load(configFilePath)
        } catch (exception: FileNotFoundException) {
            cliManager.cliOut(exception.message.toString())
            exitProcess(0)
        }

        return this
    }

    override fun start(): Unit
    {
        isRunning = true
        Microtik.app = this
    }

    override fun stop(): Unit
    {
        isRunning = false
        cliManager.cliOut("Пока!")
    }

    protected fun authorization(): Unit
    {
        var attempts = 3;

        while (attempts != 0) {
            val login = cliManager.cliIn("login")
            val password = cliManager.cliIn("password")

            if (login.equals(credentials["login"]) && password.equals(credentials["password"])) {
                cliManager.cliOut("Успешно!")
                start()
                return
            } else {
                cliManager.cliOut("Неверные данные авторизации!")
                attempts--
            }
        }
    }
}