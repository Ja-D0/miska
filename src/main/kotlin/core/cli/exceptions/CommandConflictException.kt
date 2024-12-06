package com.microtik.core.cli.exceptions

class CommandConflictException(override val message: String?) : RuntimeException(message)
