package com.microtik.core.api.responseModels

import com.google.gson.annotations.SerializedName

class AddressPut(
    @SerializedName("address")
    val address: String,
    @SerializedName("interface")
    val interfaces: String
)