package com.miska.core.server

import com.google.gson.annotations.SerializedName

data class AlertRequest(
    @SerializedName("event_type")
    val eventType: String,
    val timestamp: String,
    val host: String,
    val protocol: String,
    @SerializedName("app_protocol")
    val appProtocol: String,
    @SerializedName("src_ip")
    val srcIp: String,
    @SerializedName("src_port")
    val srcPort: Int,
    @SerializedName("dest_ip")
    val destIp: String,
    @SerializedName("dest_port")
    val destPort: Int,
    @SerializedName("signature_id")
    val signatureId: Int,
    val severity: Int,
    val signature: String,
    val category: String
)