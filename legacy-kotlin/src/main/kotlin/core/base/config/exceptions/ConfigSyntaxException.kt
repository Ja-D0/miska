package com.miska.core.base.config.exceptions

import com.miska.core.base.cli.exceptions.ApplicationException

class ConfigSyntaxException(override val message: String) : ApplicationException(message, true)
