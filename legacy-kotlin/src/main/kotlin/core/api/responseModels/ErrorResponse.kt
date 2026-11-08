package com.miska.core.api.responseModels

data class ErrorResponse(
    val error: Int,
    val message: String,
    val detail: String
) : Response
