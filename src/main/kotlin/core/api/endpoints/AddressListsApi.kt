package com.microtik.core.api.endpoints

import com.microtik.core.api.requestModels.AddressListPayload
import com.microtik.core.api.responseModels.AddressListsResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.PUT
import retrofit2.http.Path

interface AddressListsApi : Api {
    @GET("ip/firewall/address-list")
    fun print(): Call<ArrayList<AddressListsResponse>>

    @PUT("ip/firewall/address-list")
    fun add(@Body payload: AddressListPayload): Call<AddressListsResponse>

    @PATCH("ip/firewall/address-list/*{id}")
    fun edit(@Path("id") id: String, @Body payload: AddressListPayload): Call<AddressListsResponse>

    @DELETE("ip/firewall/address-list/*{id}")
    fun remove(@Path("id") id: String): Call<Unit>
}
