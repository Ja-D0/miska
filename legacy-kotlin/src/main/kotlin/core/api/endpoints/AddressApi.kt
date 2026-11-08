package com.miska.core.api.endpoints

import com.miska.core.api.requestModels.AddressPayload
import com.miska.core.api.responseModels.AddressResponse
import retrofit2.Call
import retrofit2.http.*

interface AddressApi : Api {
    @GET("ip/address")
    fun print(): Call<ArrayList<AddressResponse>>

    @PUT("ip/address")
    fun add(@Body payload: AddressPayload): Call<AddressResponse>

    @PATCH("ip/address/*{id}")
    fun edit(@Path("id") id: String, @Body payload: AddressPayload): Call<AddressResponse>

    @DELETE("ip/address/*{id}")
    fun remove(@Path("id") id: String): Call<Unit>
}
