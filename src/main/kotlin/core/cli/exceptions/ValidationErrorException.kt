package com.microtik.core.cli.exceptions

class ValidationErrorException(override val message: String = "") : RuntimeException(message)
