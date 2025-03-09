package com.microtik.core.base.cli.exceptions

/**
 *
 */
class CommandConflictException(
    override val message: String?,
    override val criticalError: Boolean = true
) : ApplicationException(message, criticalError)
