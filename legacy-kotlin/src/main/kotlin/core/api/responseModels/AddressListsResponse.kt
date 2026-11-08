package com.miska.core.api.responseModels

import com.google.gson.annotations.SerializedName

data class AddressListsResponse(
    @SerializedName(".id")
    val id: String? = null,
    @SerializedName("address")
    val address: String? = null,
    @SerializedName("creation-time")
    val creationTime: String? = null,
    @SerializedName("disabled")
    val disabled: Boolean? = null,
    @SerializedName("dynamic")
    val dynamic: Boolean? = null,
    @SerializedName("list")
    val list: String? = null,
    @SerializedName("timeout")
    val timeout: String? = null,
) : Response {
    override fun toString(): String {
        return "ID: $id, Address: $address, Creation-time: $creationTime, Disabled: $disabled, Dynamic: $dynamic, " +
                "List: $list, Timeout: $timeout"
    }
}
