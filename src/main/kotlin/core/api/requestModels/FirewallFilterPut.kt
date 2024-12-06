package com.microtik.core.api.requestModels

import com.google.gson.annotations.SerializedName

data class FirewallFilterPut(
    @SerializedName("action")
    val action: String,
    @SerializedName("chain")
    val chain: String,
    @SerializedName("src-address")
    val srcAddress: String? = null,
    @SerializedName("dst-address")
    val dstAddress: String? = null,
    @SerializedName("src-address-list")
    val srcAddressList: String? = null,
    @SerializedName("dst-address-list")
    val dstAddressList: String? = null,
    @SerializedName("protocol")
    val protocol: String? = null,
    @SerializedName("src-port")
    val srcPort: String? = null,
    @SerializedName("dst-port")
    val dstPort: String? = null,
    @SerializedName("port")
    val port: String? = null,
    @SerializedName("in-interface")
    val inInterface: String? = null,
    @SerializedName("out-interface")
    val outInterface: String? = null,
    @SerializedName("in-interface-list")
    val inInterfaceList: String? = null,
    @SerializedName("out-interface-list")
    val outInterfaceList: String? = null,
    @SerializedName("log")
    val log: Boolean? = null,
    @SerializedName("log-prefix")
    val logPrefix: String? = null,
)