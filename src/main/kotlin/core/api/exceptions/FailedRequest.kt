package com.microtik.core.api.exceptions

class FailedRequest(
    val statusCode: Int,
    val responseBody: String? = null,
    override val message: String? = null
): RuntimeException(message)