package com.microtik.core.base.cli.exceptions

/**
 * Исключение возникающее при невозможности определить наименование текущий каталога пользователя
 *
 * @author Денис Чемерис
 * @since 0.0.1
 */
class AnnotationNotFoundException(
    override val message: String?,
    override val criticalError: Boolean = true
) : ApplicationException(message, criticalError)
