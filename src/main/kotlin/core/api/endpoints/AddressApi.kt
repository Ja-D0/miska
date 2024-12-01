package com.microtik.core.api.endpoints

import com.microtik.core.api.responseModels.AddressPrint
import com.microtik.core.api.responseModels.AddressPut
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

interface AddressApi: Api {
    @GET("ip/address")
    fun print(): Call<ArrayList<AddressPrint>>

    @PUT("ip/address")
    fun add(@Body addressPut: AddressPut): Call<AddressPrint>

    @DELETE("ip/address/*{id}")
    fun remove(@Path("id") id: String): Call<Void>
}