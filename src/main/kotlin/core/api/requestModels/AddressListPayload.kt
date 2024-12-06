package com.microtik.core.api.requestModels

data class AddressListPayload(
    val list: String? = null,
    val address: String? = null,
) : Payload
