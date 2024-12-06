package com.microtik.core.api

import com.microtik.Microtik
import com.microtik.core.api.endpoints.AddressApi
import com.microtik.core.api.endpoints.AddressListsApi
import com.microtik.core.api.endpoints.FirewallFilterApi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import java.util.concurrent.TimeUnit

class MicrotikApiService private constructor() {
    companion object {
        private var instance: MicrotikApiService? = null
        private lateinit var retrofit: Retrofit

        fun getInstance(): MicrotikApiService {
            if (instance == null) {
                instance = MicrotikApiService()
            }

            return instance!!
        }
    }

    init {
        val client: OkHttpClient = OkHttpClient.Builder()
            .readTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(120, TimeUnit.SECONDS)
            .connectTimeout(120, TimeUnit.SECONDS)
            .addInterceptor { chain ->
                chain.proceed(chain.request().newBuilder().header("Authorization", getBasicCredentials()).build())
            }
            .addInterceptor(HttpLoggingInterceptor { message -> println(message) }.apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()

        retrofit = Retrofit.Builder()
            .baseUrl("http://" + Microtik.app.getConfig().microtikApiConfig.microtikServerConfig.host + "/rest/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }

    private fun getBasicCredentials(): String {
        val login = Microtik.app.getConfig().microtikApiConfig.microtikServerConfig.login
        val password = Microtik.app.getConfig().microtikApiConfig.microtikServerConfig.password

        return "Basic " + Base64.getEncoder().encodeToString("$login:$password".encodeToByteArray())
    }

    fun getAddressApi(): AddressApi = retrofit.create(AddressApi::class.java)

    fun getAddressListsApi(): AddressListsApi = retrofit.create(AddressListsApi::class.java)

    fun getFirewallFilterApi(): FirewallFilterApi = retrofit.create(FirewallFilterApi::class.java)
}
