package com.microtik.core.api.responseModels

import com.google.gson.annotations.SerializedName

data class AddressResponse(
    @SerializedName(".id")
    val id: String,
    @SerializedName("actual-interface")
    val actualInterface: String,
    @SerializedName("address")
    val address: String,
    @SerializedName("disabled")
    val disabled: Boolean,
    @SerializedName("dynamic")
    val dynamic: Boolean,
    @SerializedName("interface")
    val _interface: String,
    @SerializedName("invalid")
    val invalid: Boolean,
    @SerializedName("network")
    val network: String,
) : Response {
    override fun toString(): String {
        return "ID: $id, Actual Interface: $actualInterface, Address: $address, Disabled: $disabled, Dynamic: $dynamic, Interface: $_interface, Invalid: $invalid, Network: $network"
    }
}
