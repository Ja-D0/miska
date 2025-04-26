package com.miska.core.api

import com.google.gson.Gson
import com.miska.Miska
import com.miska.core.api.endpoints.AddressApi
import com.miska.core.api.endpoints.AddressListsApi
import com.miska.core.api.endpoints.FirewallFilterApi
import com.miska.core.api.exceptions.FailedRequestException
import com.miska.core.api.responseModels.ErrorResponse
import com.miska.core.base.logger.FullHttpLoggingInterceptor
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.ConnectException
import java.util.*
import java.util.concurrent.TimeUnit

class MikrotikApiService private constructor() {
    companion object {
        private var instance: MikrotikApiService? = null
        private lateinit var retrofit: Retrofit

        fun getInstance(): MikrotikApiService {
            if (instance == null) {
                instance = MikrotikApiService()
            }

            return instance!!
        }

        /**
         *
         */
        fun <T> runRequest(callable: () -> Response<T>): T {
            val response: Response<T>

            try {
                response = callable()
            } catch (connectException: ConnectException) {
                throw FailedRequestException(500, "", connectException.message!!)
            }

            if (response.isSuccessful) {
                return response.body()!!

            } else {
                if (response.errorBody() != null) {
                    val errorBody = Gson().fromJson(response.errorBody()!!.string(), ErrorResponse::class.java)

                    throw FailedRequestException(errorBody.error, errorBody.detail, errorBody.message)
                }

                throw FailedRequestException(response.code(), response.body().toString(), response.message())
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

        if (Miska.app.getConfig().logsConfig.httpLogsConfig.path.isNotBlank()) {
            builder.addInterceptor(FullHttpLoggingInterceptor { message -> Miska.http(message) }.apply {
                level = FullHttpLoggingInterceptor.Level.BODY
            })
        }

        val client = builder.build()

        retrofit = Retrofit.Builder()
            .baseUrl("http://" + Miska.app.getConfig().mikrotikApiConfig.mikrotikServerConfig.host + "/rest/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }

    private fun getBasicCredentials(): String {
        val login = Miska.app.getConfig().mikrotikApiConfig.mikrotikServerConfig.login
        val password = Miska.app.getConfig().mikrotikApiConfig.mikrotikServerConfig.password

        return "Basic " + Base64.getEncoder().encodeToString("$login:$password".encodeToByteArray())
    }

    fun getAddressApi(): AddressApi = retrofit.create(AddressApi::class.java)

    fun getAddressListsApi(): AddressListsApi = retrofit.create(AddressListsApi::class.java)

    fun getFirewallFilterApi(): FirewallFilterApi = retrofit.create(FirewallFilterApi::class.java)
}
