package com.microtik.core.base.cli.exceptions

/**
 *
 */
class CommandConflictException(override val message: String?) : ApplicationException(message, true)
