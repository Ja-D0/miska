package com.microtik.core.api.responseModels

import com.google.gson.annotations.SerializedName
//TODO: Подумать об объединении моделей print и add
data class FirewallFilterPrint(
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
    val logPrefix: String,
    @SerializedName("packets")
    val packets: String,
    @SerializedName("dst-address")
    val dstAddress: String,
    @SerializedName("dst-address-list")
    val dstAddressList: String,
    @SerializedName("dst-port")
    val dstPort: String,
    @SerializedName("in-interface")
    val inInterface: String,
    @SerializedName("in-interface-list")
    val inInterfaceList: String,
    @SerializedName("out-interface")
    val outInterface: String,
    @SerializedName("out-interface-list")
    val outInterfaceList: String,
    @SerializedName("port")
    val port: String,
    @SerializedName("protocol")
    val protocol: String,
    @SerializedName("src-address")
    val srcAddress: String,
    @SerializedName("src-address-list")
    val srcAddressList: String,
    @SerializedName("src-port")
    val srcPort: String
) {
    override fun toString(): String {
        return "ID: $id, Action: $action, Bytes: $bytes, Chain: $chain, Disabled: $disabled, Dynamic: $dynamic, " +
                "Invalid: $invalid, Log: $log, Log-prefix: $logPrefix, Packets: $packets, Dst Address: $dstAddress, " +
                "Dst Address List: $dstAddressList, Dst Port: $dstPort, In Interface: $inInterface, In Interface List: " +
                "$inInterfaceList, Out Interface: $outInterface, Out Interface List: $outInterfaceList, Port: $port, " +
                "Protocol: $protocol, Src Address: $srcAddress, Src Address List: $srcAddressList, Src Port: $srcPort"
    }
}