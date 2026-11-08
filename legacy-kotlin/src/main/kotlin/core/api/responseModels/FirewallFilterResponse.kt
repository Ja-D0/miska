package com.miska.core.api.responseModels

import com.google.gson.annotations.SerializedName

data class FirewallFilterResponse(
    @SerializedName(".id")
    val id: String,
    @SerializedName("action")
    val action: String,
    @SerializedName("bytes")
    val bytes: String,
    @SerializedName("chain")
    val chain: String,
    @SerializedName("disabled")
    val disabled: Boolean,
    @SerializedName("dynamic")
    val dynamic: Boolean,
    @SerializedName("invalid")
    val invalid: Boolean,
    @SerializedName("log")
    val log: Boolean,
    @SerializedName("log-prefix")
    val logPrefix: String? = null,
    @SerializedName("packets")
    val packets: String? = null,
    @SerializedName("dst-address")
    val dstAddress: String? = null,
    @SerializedName("dst-address-list")
    val dstAddressList: String? = null,
    @SerializedName("dst-port")
    val dstPort: String? = null,
    @SerializedName("in-interface")
    val inInterface: String? = null,
    @SerializedName("in-interface-list")
    val inInterfaceList: String? = null,
    @SerializedName("out-interface")
    val outInterface: String? = null,
    @SerializedName("out-interface-list")
    val outInterfaceList: String? = null,
    @SerializedName("port")
    val port: String? = null,
    @SerializedName("protocol")
    val protocol: String? = null,
    @SerializedName("src-address")
    val srcAddress: String? = null,
    @SerializedName("src-address-list")
    val srcAddressList: String? = null,
    @SerializedName("src-port")
    val srcPort: String? = null,
    @SerializedName("comment")
    val comment: String? = null
) : Response {
    override fun toString(): String {
        return "ID: $id, Action: $action, Bytes: $bytes, Chain: $chain, Disabled: $disabled, Dynamic: $dynamic, " +
                "Invalid: $invalid, Log: $log, Log-prefix: $logPrefix, Packets: $packets, Dst Address: $dstAddress, " +
                "Dst Address List: $dstAddressList, Dst Port: $dstPort, In Interface: $inInterface, In Interface List: " +
                "$inInterfaceList, Out Interface: $outInterface, Out Interface List: $outInterfaceList, Port: $port, " +
                "Protocol: $protocol, Src Address: $srcAddress, Src Address List: $srcAddressList, Src Port: $srcPort" +
                "Comment: $comment"
    }
}
