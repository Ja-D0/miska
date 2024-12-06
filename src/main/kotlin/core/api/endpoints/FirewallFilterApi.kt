package com.microtik.core.api.endpoints

import com.microtik.core.api.requestModels.FirewallFilterPayload
import com.microtik.core.api.responseModels.FirewallFilterResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.PUT
import retrofit2.http.Path

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
