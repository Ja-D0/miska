package com.microtik.core.api.endpoints

import com.microtik.core.api.requestModels.AddressPayload
import com.microtik.core.api.responseModels.AddressResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.PUT
import retrofit2.http.Path

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
