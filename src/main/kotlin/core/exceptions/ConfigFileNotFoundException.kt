package com.microtik.core.exceptions

import java.io.FileNotFoundException

class ConfigFileNotFoundException(override val message: String?): FileNotFoundException(message)