package com.currency.converter.app.network

import com.currency.converter.app.model.Exchange
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.Query

interface ExchangeApi {


    @GET("v1/latest")
    suspend fun getRates (@Query("access_key") accessKey : String ): Response<Exchange>


}