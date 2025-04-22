package com.miska.core.base.suricata

import com.miska.Miska

fun Any.info(message: String) = Miska.info(message, "suricata-info")

fun Any.error(message: String) = Miska.error(message, "suricata-info")

fun Any.alert(message: String) = Miska.alert(message.trimIndent(), "suricata-alert")