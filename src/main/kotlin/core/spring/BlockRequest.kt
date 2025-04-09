package com.miska.core.spring

import kotlinx.serialization.Serializable

@Serializable
data class BlockRequest(
    val src_ip: String = "",
    val dst_ip: String = "",
    val signature: String = "",
)