package com.miska.core.server

import com.google.gson.annotations.SerializedName

data class AlertRequest(
    val timestamp: String,
    @SerializedName("src_ip")
    val srcIp: String,
    @SerializedName("dest_ip")
    val destIp: String,
    @SerializedName("signature_id")
    val signatureId: Long,
    val severity: Int,
    val signature: String,
    val category: String,
    val payload: String
)