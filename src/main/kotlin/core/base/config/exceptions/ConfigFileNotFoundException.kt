package com.miska.core.base.config.exceptions

import java.io.FileNotFoundException

class ConfigFileNotFoundException(override val message: String?) : FileNotFoundException(message)
