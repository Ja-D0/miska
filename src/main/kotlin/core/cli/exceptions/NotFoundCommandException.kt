package com.microtik.core.cli.exceptions

class NotFoundCommandException(override val message: String?): RuntimeException(message)