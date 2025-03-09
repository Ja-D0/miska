package com.microtik.core.base.cli

import com.microtik.Microtik
import com.microtik.core.base.cli.interfaces.Request

/**
 * Класс, реализующий запрос на выполнение команды
 *
 * @author Виктория Яковлева
 * @since 0.0.1
 */
class Request : Request {

    /**
     * Хранит команду и связанные с ней параметры
     *
     * @author Виктория Яковлева
     * @since 0.0.1
     */

    private val params: ArrayList<String> = ArrayList()

    init {
        waitCommand()
    }

    /**
     * Разрешает запрос команды и связанных параметров
     *
     * @return экземпляр [Pair], первым элементом является запрашиваемая команда, вторым элементом [ArrayList] являются связанные параметры
     * @author Виктория Яковлева
     * @since 0.0.1
     */
    override fun resolve(): Pair<String, ArrayList<String>> {
        return params.first() to ArrayList(params.drop(1))
    }

    /**
     * Инициализирует ожидание команды от пользователя и заполняет [params] данными команды пользователя
     *
     * @author Виктория Яковлева
     * @since 0.0.1
     */
    private fun waitCommand() {
        var line: String?

        do {
            line = Microtik.app.cliIn()
        } while (line.isNullOrBlank())

        line.trim()

        if (line.last() == '/') {
            line = line.dropLast(1)
        }

        val separatedLine = line.split(" ")

        params.add(separatedLine.first())

        if (separatedLine.size > 1) {
            params.addAll(separatedLine.drop(1))
        }
    }
}