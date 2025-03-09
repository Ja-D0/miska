package com.microtik.core.base.config.exceptions

import com.microtik.core.base.cli.exceptions.ApplicationException

class ConfigSyntaxException(override val message: String?) : ApplicationException(message, true)
