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
) {
    override fun hashCode(): Int {
        return srcIp.hashCode() * 31 +
                destIp.hashCode() * 31 +
                signatureId.hashCode() * 31 +
                severity.hashCode() * 31 +
                signature.hashCode() * 31 +
                category.hashCode() // Исключаем timestamp
    }
}