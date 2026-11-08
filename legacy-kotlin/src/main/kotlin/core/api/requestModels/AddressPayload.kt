package com.miska.core.api.requestModels

import com.google.gson.annotations.SerializedName

data class AddressPayload(
    @SerializedName("address")
    val address: String? = null,
    @SerializedName("interface")
    val interfaces: String? = null,
    @SerializedName("network")
    val network: String? = null,
    @SerializedName("disabled")
    val disabled: Boolean? = null
) : Payload
