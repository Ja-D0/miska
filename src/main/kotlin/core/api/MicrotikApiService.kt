package com.microtik.core.api

import com.microtik.Microtik
import com.microtik.core.api.endpoints.AddressApi
import com.microtik.core.api.endpoints.AddressListsApi
import com.microtik.core.api.endpoints.FirewallFilterApi
import com.microtik.core.api.exceptions.FailedRequest
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
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

        /**
         *
         */
        fun <T> runRequest(callable: () -> Response<T>): T {
            val response = callable()

            if (response.isSuccessful && response.body() != null) {
                return response.body() as T
            } else {
                throw FailedRequest(response.code(), response.body().toString(), response.message())
            }
        }
    }

    init {
        val builder: OkHttpClient.Builder = OkHttpClient.Builder()
            .readTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(120, TimeUnit.SECONDS)
            .connectTimeout(120, TimeUnit.SECONDS)
            .addInterceptor { chain ->
                chain.proceed(chain.request().newBuilder().header("Authorization", getBasicCredentials()).build())
            }

//        if (System.getProperty("APP_ENV_DEBUG").toBoolean()) {
        builder.addInterceptor(HttpLoggingInterceptor { message -> println(message) }.apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
//        }

        val client = builder.build()

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
