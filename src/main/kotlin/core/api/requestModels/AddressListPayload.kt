package com.miska.core.api.requestModels

import com.google.gson.annotations.SerializedName

data class AddressListPayload(
    @SerializedName("list")
    val list: String? = null,
    @SerializedName("address")
    val address: String? = null,
    @SerializedName("timeout")
    val timeout: String? = null,
    @SerializedName("disabled")
    val disabled: Boolean? = null,
) : Payload
