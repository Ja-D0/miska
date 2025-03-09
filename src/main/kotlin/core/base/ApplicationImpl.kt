package com.microtik.core.base

import com.microtik.Microtik
import com.microtik.core.base.cli.CommandsListImpl
import com.microtik.core.base.cli.InlineCommandsList
import com.microtik.core.base.cli.Request
import com.microtik.core.base.cli.annotations.CommandList
import com.microtik.core.base.cli.exceptions.AnnotationNotFoundException
import com.microtik.core.base.cli.exceptions.ApplicationException
import com.microtik.core.base.cli.exceptions.CommandsListNotFoundException
import com.microtik.core.base.cli.interfaces.Response
import com.microtik.core.base.interfaces.Application
import com.microtik.core.base.interfaces.Configurable
import com.microtik.core.commandLists.RootCommandsList
import java.io.FileNotFoundException
import kotlin.reflect.full.findAnnotation
import com.microtik.core.base.cli.Response as ResponseImpl

/**
 * Класс, содержащий базовую реализацию приложения
 *
 * @author Денис Чемерис
 * @since 0.0.1
 */
abstract class ApplicationImpl(configFilePath: String? = null) : Application, Configurable {
    private var isRunning: Boolean = false
    private lateinit var config: Config
    private val traceCommandsLists: MutableList<CommandsListImpl> = mutableListOf(RootCommandsList())
    private val inlineCommandsList: InlineCommandsList = InlineCommandsList()
    private var currentPath: String

    init {
        loadConfig(configFilePath)
        currentPath = getCommandsListPath(getCurrentCommandsList())
    }

    /**
     * Возвращает путь указанного экземпляра [CommandsListImpl]
     *
     * @return путь [String]
     * @throws [AnnotationNotFoundException], если объект каталога не содержит определения для пути
     * @author Денис Чемерис
     * @since 0.0.1
     */
    private fun getCommandsListPath(commandsList: CommandsListImpl): String {
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
            cliOut(exception.message!!)
            stop()
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
    fun runCommand(command: String, params: ArrayList<String>): Response {
        val commands = prepareCommand(command).split("/")
        var response = ResponseImpl()

        for (commandId in commands) {
            if (inlineCommandsList.isInlineCommand(commandId)) {
                inlineCommandsList.runCommand(commandId, params)
                continue
            }

            if (getCurrentCommandsList().isPath(commandId)) {
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
    fun getCurrentCommandsList() =
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
    fun goToCommandsList(newCommandsList: CommandsListImpl) {
        traceCommandsLists.add(newCommandsList)
        currentPath += "/${getCommandsListPath(newCommandsList)}"
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
    fun goBack(): Unit {
        if (traceCommandsLists.size != 1) {
            currentPath = currentPath.dropLast(getCommandsListPath(getCurrentCommandsList()).length + 1)
            traceCommandsLists.removeLast()
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
                    cliOut("!!! ${exception.message!!}")
                    stop()
                } else {
                    cliOut(exception.message!!)
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
        Microtik.app = this
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
    }

    /**
     * Регистрирует запрос приложения на ожидание данных от пользователя
     *
     * @author Денис Чемерис
     * @since 0.0.1
     */
    fun cliIn(message: String? = null): String? {
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
    fun cliOut(message: String): Unit = println(message)
}
