package com.microtik.core.api.requestModels

import com.google.gson.annotations.SerializedName

data class FirewallFilterPut(
    @SerializedName("chain")
    val chain: String,
    @SerializedName("action")
    val action: String,
)