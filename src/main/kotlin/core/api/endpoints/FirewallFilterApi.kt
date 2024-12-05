package com.microtik.core.api.endpoints

import com.microtik.core.api.requestModels.FirewallFilterPut
import com.microtik.core.api.responseModels.FirewallFilterPrint
import retrofit2.Call
import retrofit2.http.*

interface FirewallFilterApi: Api {

    @GET("ip/firewall/filter")
    fun print(): Call<ArrayList<FirewallFilterPrint>>

    @PUT("ip/firewall/filter")
    fun add(@Body firewallFilterPut: FirewallFilterPut): Call<FirewallFilterPrint>

    @DELETE("ip/firewall/filter/*{id}")
    fun remove(@Path("id") id: String): Call<Void>
}