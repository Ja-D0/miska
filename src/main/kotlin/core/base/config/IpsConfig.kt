package com.miska.core.base.config

import com.google.gson.annotations.SerializedName

data class IpsConfig(
    @SerializedName("auto_start_server")
    val autoStartServer: Boolean = false,
    @SerializedName("rules_filename")
    val rulesFilename: String = "rules.json",
    @SerializedName("rules_filepath")
    val rulesFilepath: String = "rules/",
    @SerializedName("address_list_name")
    val addressListName: String = "blocked-suricata",
    @SerializedName("repeat_threshold")
    val repeatThreshold: Long = 5000,
    @SerializedName("repeat_request_count")
    val repeatRequestCount: Int = 5,
    @SerializedName("mikrotik_in_interface")
    val mikrotikInInterface: String = "ether1",
) : AbstractConfig()
