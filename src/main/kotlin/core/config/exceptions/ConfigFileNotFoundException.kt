package com.microtik.core.config.exceptions

import java.io.FileNotFoundException

class ConfigFileNotFoundException(override val message: String?) : FileNotFoundException(message)
