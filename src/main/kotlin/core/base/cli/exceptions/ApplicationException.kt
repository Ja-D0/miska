package com.microtik.core.base.cli.exceptions

/**
 * Основной класс исключения, от которого наследуются все исключения приложения
 *
 * @author Денис Чемерис
 * @since 0.0.1
 */
open class ApplicationException(
    override val message: String?,

    /**
     * Указывает, является ли ошибка критичной, если это так - приложение прекратит свою работу
     *
     * @author Денис Чемерис
     * @since 0.0.1
     */
    open val criticalError: Boolean = false
) : RuntimeException()
