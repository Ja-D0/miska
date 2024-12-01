package com.microtik.core.api.requestModels

import com.google.gson.annotations.SerializedName

data class AddressPut(
    @SerializedName("address")
    val address: String,
    @SerializedName("interface")
    val interfaces: String
)