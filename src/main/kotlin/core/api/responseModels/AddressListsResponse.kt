package com.microtik.core.api.responseModels

import com.google.gson.annotations.SerializedName

data class AddressListsResponse(
    @SerializedName(".id")
    val id: String,
    @SerializedName("address")
    val address: String,
    @SerializedName("creation-time")
    val creationTime: String,
    @SerializedName("disabled")
    val disabled: Boolean,
    @SerializedName("dynamic")
    val dynamic: Boolean,
    @SerializedName("list")
    val list: String,
    @SerializedName("timeout")
    val timeout: String? = null,
) : Response {
    override fun toString(): String {
        return "ID: $id, Address: $address, Creation-time: $creationTime, Disabled: $disabled, Dynamic: $dynamic, " +
                "List: $list, Timeout: $timeout"
    }
}
