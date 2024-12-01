package com.microtik.core.api.endpoints

import com.microtik.core.api.requestModels.AddressListPut
import com.microtik.core.api.responseModels.AddressListsPrint
import retrofit2.Call
import retrofit2.http.*

interface AddressListsApi: Api {
    @GET("ip/firewall/address-list")
    fun print(): Call<ArrayList<AddressListsPrint>>

    @PUT("ip/firewall/address-list")
    fun add(@Body addressPut: AddressListPut): Call<AddressListsPrint>

    @DELETE("ip/firewall/address-list/*{id}")
    fun remove(@Path("id") id: String): Call<Void>
}