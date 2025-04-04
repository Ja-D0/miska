package com.miska.core.api.exceptions

import com.miska.core.base.cli.exceptions.ApplicationException

class FailedRequestException(
    val statusCode: Int,
    val detail: String,
    override val message: String
) : ApplicationException(detail) {
    override fun toString(): String {
        return "statusCode: ${statusCode}, message: ${message}, detail: $detail"
    }
}
