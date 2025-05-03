package com.miska.core.base.suricata

import com.miska.Miska

fun Any.info(message: String) = Miska.info(message, "ips-info")

fun Any.error(message: String) = Miska.error(message, "ips-info")

fun Any.alert(message: String) = Miska.alert(message.trimIndent(), "ips-alert")

fun Long?.toDate(): String? {
    if (this == null) return null

    return String.format("%02d:%02d:%02d", this / 3600, this / 60 % 60, this % 60)
}
