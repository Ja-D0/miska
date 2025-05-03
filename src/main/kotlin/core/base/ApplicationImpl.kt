package com.miska.core.base

import com.miska.Miska
import com.miska.core.base.cli.CommandsListImpl
import com.miska.core.base.cli.InlineCommandsList
import com.miska.core.base.cli.Request
import com.miska.core.base.cli.annotations.CommandList
import com.miska.core.base.cli.exceptions.AnnotationNotFoundException
import com.miska.core.base.cli.exceptions.ApplicationException
import com.miska.core.base.cli.exceptions.CommandsListNotFoundException
import com.miska.core.base.cli.interfaces.CommandsList
import com.miska.core.base.cli.interfaces.Response
import com.miska.core.base.config.SuricataIpsConfig
import com.miska.core.base.config.logs.AbstractLogsConfig
import com.miska.core.base.interfaces.Application
import com.miska.core.base.interfaces.Configurable
import com.miska.core.base.logger.Dispatcher
import com.miska.core.base.logger.DispatcherImpl
import com.miska.core.base.logger.FileTarget
import com.miska.core.base.logger.TelegramBotTarget
import com.miska.core.commandLists.RootCommandsList
import com.miska.core.server.KtorServer
import java.io.FileNotFoundException
import kotlin.properties.Delegates
import kotlin.reflect.full.findAnnotation
import kotlin.system.exitProcess
import com.miska.core.base.cli.Response as ResponseImpl

/**
 * Класс, содержащий базовую реализацию приложения
 *
 * @author Денис Чемерис
 * @since 0.0.1
 */
abstract class ApplicationImpl(configFilePath: String? = null) : Application, Configurable {
    private var isRunning: Boolean = false
    private lateinit var config: Config
    private val traceCommandsLists: MutableList<CommandsList> = mutableListOf(RootCommandsList())
    private val inlineCommandsList: CommandsList = InlineCommandsList()
    private var currentPath: String

    private var server by Delegates.notNull<KtorServer>()

    init {
        loadConfig(configFilePath)
        initLogger(config)

        currentPath = getCommandsListPath(getCurrentCommandsList())
    }

    private fun initLogger(config: Config) {
        val dispatcher = DispatcherImpl()
        try {
            dispatcher.apply {
                registerLogFromConfig(config.logsConfig.appLogsConfig)
                registerLogFromConfig(config.logsConfig.alertLogsConfig)
                registerLogFromConfig(config.logsConfig.ipsLogsConfig)
                registerLogFromConfig(config.logsConfig.httpLogsConfig)

                if (config.logsConfig.alertLogsConfig.telegramBotConfig.token.isNotEmpty()) {
                    registerTarget {
                        TelegramBotTarget(
                            config.logsConfig.alertLogsConfig.telegramBotConfig,
                            listOf("alert"),
                            listOf("ips-alert")
                        )
                    }
                }

                setLogger {
                    Miska.logger
                }
            }
        } catch (e: Exception) {
            cliOut(e.message ?: "Unknown error")
            exitProcess(1)
        }
    }

    private fun Dispatcher.registerLogFromConfig(config: AbstractLogsConfig) {
        if (config.enable) {
            registerTarget { FileTarget(config.filename, config.path, config.levels, config.categories) }
        }
    }

    /**
     * Возвращает путь указанного экземпляра [CommandsListImpl]
     *
     * @return путь [String]
     * @throws [AnnotationNotFoundException], если объект каталога не содержит определения для пути
     * @author Денис Чемерис
     * @since 0.0.1
     */
    private fun getCommandsListPath(commandsList: CommandsList): String {
        val annotation = commandsList::class.findAnnotation<CommandList>()
            ?: throw AnnotationNotFoundException(
                "It is necessary to specify the CommandList annotation for ${commandsList::class.qualifiedName}"
            )

        return annotation.path
    }


    /**
     * Возвращает [Config], содержащий в себе конфигурацию приложения
     *
     * @return экземпляр [Config]
     * @author Денис Чемерис
     * @since 0.0.1
     */
    fun getConfig() = config

    /**
     * Загружает файл конфигурации по пути [configFilePath] в приложение.
     *
     * Если [configFilePath] не указан, конфигурационный файл будет искаться по пути по умолчанию
     *
     * @param configFilePath путь до файла конфигурации
     * @throws FileNotFoundException, если файл конфигурации не найден
     */
    final override fun loadConfig(configFilePath: String?) {
        try {
            config = ConfigLoader().load(configFilePath)
        } catch (exception: FileNotFoundException) {
            cliOut(exception.message ?: "Config file not found")
            exitProcess(1)
        } catch (applicationException: ApplicationException) {
            cliOut(applicationException.message)
            exitProcess(1)
        }
    }

    /**
     * Запускает выполнение введенных команд пользователя. Этот метод анализирует введенную команду и запускает
     * [CommandsListImpl.runCommand] для перехода в указанный каталог, либо получения результат выполнения встроенного
     * действия в текущем каталоге. Данным метод также проверяет соответствие введенной команды и встроенной. Если она
     * таковой является, будет запущена встроенная команда.
     *
     * @return экземпляр ответа [Response], содержащий ответ команды
     * @author Денис Чемерис
     * @since 0.0.1
     */
    override fun runCommand(command: String, params: ArrayList<String>): Response {
        val commands = prepareCommand(command).split("/")
        var response = ResponseImpl()

        for (commandId in commands) {
            if (inlineCommandsList.hasCommand(commandId)) {
                inlineCommandsList.runCommand(commandId, params)
                continue
            }

            if (getCurrentCommandsList().commandIsPath(commandId)) {
                val result = getCurrentCommandsList().runCommand(commandId, ArrayList())

                if (result !is CommandsListImpl) {
                    throw ApplicationException(
                        "A command of type \"path\" must return a class instance" + CommandsListImpl::class.qualifiedName,
                        true
                    )
                }

                goToCommandsList(result)
            } else {
                val result = getCurrentCommandsList().runCommand(commandId, params)

                if (result is Response) {
                    response = result as ResponseImpl
                } else {
                    response.data = result.toString()
                }
            }
        }

        return response
    }

    /**
     * Возвращает экземпляр текущего каталога, в котором находится пользователь
     *
     * @return экземпляр [CommandsListImpl]
     * @throws [CommandsListNotFoundException], если текущий каталог пользователя не найден
     * @author Денис Чемерис
     * @since 0.0.1
     */
    override fun getCurrentCommandsList() =
        traceCommandsLists.lastOrNull() ?: throw CommandsListNotFoundException("Current commands list not found")

    private fun prepareCommand(command: String): String {
        var normalizedCommand: String = command

        if (command.last() == '/') {
            normalizedCommand = command.dropLast(1)
        }

        return normalizedCommand.trim()
    }

    /**
     * Переходит в каталог экземпляра [newCommandsList], после этого он становится активным и можно выполнять его
     * встроенные команды с помощью [CommandsListImpl.runCommand]
     *
     * Также данный метод изменяет текущий каталог [currentPath] для вывода пользователю
     *
     * @return [Unit]
     * @author Денис Чемерис
     * @since 0.0.1
     */
    override fun goToCommandsList(newCommandsList: CommandsList): Boolean {
        traceCommandsLists.add(newCommandsList)
        currentPath += "/${getCommandsListPath(newCommandsList)}"

        Miska.info("Transition to a new folder: ${getCommandsListPath(newCommandsList)}")
        return true
    }

    /**
     * Переходит на каталог назад, после этого он становится активным и можно выполнять его
     * встроенные команды с помощью [CommandsListImpl.runCommand]
     *
     * Также данный метод изменяет текущий каталог [currentPath] для вывода пользователю
     *
     * @return [Unit]
     * @author Денис Чемерис
     * @since 0.0.1
     */
    override fun goBack(): Unit {
        if (traceCommandsLists.size != 1) {
            currentPath = currentPath.dropLast(getCommandsListPath(getCurrentCommandsList()).length + 1)
            traceCommandsLists.removeLast()

            Miska.info("Transition back")
        }
    }

    /**
     * Запускает приложение
     *
     * @author Денис Чемерис
     * @since 0.0.1
     */
    override fun run() {
        start()

        while (isRunning) {
            try {
                handleCommandRequest(Request()).send()
            } catch (exception: ApplicationException) {
                if (exception.criticalError) {
                    cliOut("!!! $exception")
                    stop()
                } else {
                    cliOut(exception.message)
                }
            }
        }
    }

    /**
     * Запускает приложения для выполнения команд пользователя
     *
     * @see [stop]
     * @author Денис Чемерис
     * @since 0.0.1
     */
    override fun start() {
        isRunning = true
        Miska.app = this

        autoRunAnalyzeAlertServer()
    }

    //TODO: Добавить поведение с событиями и состояниями приложения

    /**
     * Автоматически запускает Ktor сервер при запуске приложения, если есть для этого основания
     *
     * @see [SuricataIpsConfig.autoStartServer]
     * @author Денис Чемерис
     * @since 0.0.1
     */
    private fun autoRunAnalyzeAlertServer() {
        server = KtorServer()

        if (config.suricataIps.autoStartServer) {
            cliOut(startAnalyzeAlertServer())
        }
    }


    /**
     * Запускает Ktor сервер для приёма и обработки Alert
     *
     * @see [stopAnalyzeAlertServer]
     * @author Денис Чемерис
     * @since 0.0.1
     */
    fun startAnalyzeAlertServer(): String {
        if (server.isRunning()) {
            return "REST API server is already running"
        }

        return if (server.start()) {
            "REST API Server is successfully launched"
        } else {
            "It was not possible to start REST API Server"
        }
    }


    /**
     * Останавливает Ktor сервер
     *
     * @see [startAnalyzeAlertServer]
     * @author Денис Чемерис
     * @since 0.0.1
     */
    fun stopAnalyzeAlertServer(): String {
        if (!server.isRunning()) {
            return "REST API server is already stopped"
        }
        return if (server.stop()) {
            "REST API server has been successfully stopped"
        } else {
            "It was not possible to stop the REST API server"
        }
    }

    /**
     * Перезагружает правила для анализа Alerts и сервер Ktor
     *
     * @author Денис Чемерис
     * @since 0.0.1
     */
    fun reloadRulesForAnalyzeAlerts(): String {
        if (server.isRunning()) {
            stopAnalyzeAlertServer()
            server.reloadingTheAnalyzingRules()
            startAnalyzeAlertServer()

            return "Rules reloaded"
        }

        return "REST API Server is not launched"
    }

    /**
     * Останавливает приложение
     *
     * @see [start]
     * @author Денис Чемерис
     * @since 0.0.1
     */
    override fun stop(): Unit {
        isRunning = false

        stopAnalyzeAlertServer()

        exitProcess(0)
    }

    /**
     * Регистрирует запрос приложения на ожидание данных от пользователя
     *
     * @author Денис Чемерис
     * @since 0.0.1
     */
    override fun cliIn(message: String?): String? {
        if (message == null) {
            print(">>> $currentPath$ ")
        } else {
            print(">>> $message: ")
        }

        return readlnOrNull()
    }

    /**
     * Выводит данные пользователю
     *
     * @author Денис Чемерис
     * @since 0.0.1
     */
    override fun cliOut(message: String): Unit = println(message)
}
