package com.miska.core.api.endpoints

import com.miska.core.api.requestModels.FirewallFilterPayload
import com.miska.core.api.responseModels.FirewallFilterResponse
import retrofit2.Call
import retrofit2.http.*

interface FirewallFilterApi : Api {
    @GET("ip/firewall/filter")
    fun print(): Call<ArrayList<FirewallFilterResponse>>

    @PUT("ip/firewall/filter")
    fun add(@Body payload: FirewallFilterPayload): Call<FirewallFilterResponse>

    @PATCH("ip/firewall/filter/*{id}")
    fun edit(@Path("id") id: String, @Body payload: FirewallFilterPayload): Call<FirewallFilterResponse>

    @DELETE("ip/firewall/filter/*{id}")
    fun remove(@Path("id") id: String): Call<Unit>
}
