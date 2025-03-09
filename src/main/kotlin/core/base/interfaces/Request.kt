package com.microtik.core.base.interfaces

/**
 * Интерфейс для реализации запрос на выполнение команды
 *
 * @author Виктория Яковлева
 * @since 0.0.1
 */
interface Request {

    /**
     * Разрешает запрос команды и связанных параметров
     *
     * @return экземпляр [Pair], первым элементом является запрашиваемая команда, вторым элементом [ArrayList] являются связанные параметры
     * @author Виктория Яковлева
     * @since 0.0.1
     */
    fun resolve(): Pair<String, ArrayList<String>>
}