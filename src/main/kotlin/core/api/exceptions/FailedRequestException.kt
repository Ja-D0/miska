package com.microtik.core.api.exceptions

import com.microtik.core.base.cli.exceptions.ApplicationException

class FailedRequestException(
    val statusCode: Int,
    val responseBody: String? = null,
    override val message: String? = null
) : ApplicationException(message)
