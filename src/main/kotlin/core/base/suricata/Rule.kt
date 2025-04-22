package com.miska.core.base.suricata

import com.google.gson.annotations.SerializedName

data class Rule(
    val name: String = "Unknown name",
    val action: String = "skip",
    val target: String? = null,
    val timeout: Long? = null,
    @SerializedName("signatures_matches")
    val signaturesMatches: Set<String> = emptySet(),
    @SerializedName("categories_matches")
    val categoriesMatches: Set<String> = emptySet(),
    @SerializedName("signatures_ids")
    val signaturesIds: Set<Long> = emptySet()
)
