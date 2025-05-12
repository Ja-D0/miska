package com.miska.core.base.ips

import com.miska.core.server.AlertRequest

data class Verdict(
    val alert: AlertRequest,
    var action: String = "skip",
    var targets: List<String>? = null,
    var decisionRules: List<Pair<String, Rule>>? = null
)
