package com.microtik.core.base.cli.exceptions

/**
 * Исключение, которое вызывается приложением, если текущий каталог пользователя не удалось найти
 *
 * @author Денис Чемерис
 * @since 0.0.1
 */
class CommandsListNotFoundException(
    override val message: String?,
    override val criticalError: Boolean = true
) : ApplicationException(message, criticalError)
