package com.alok.groww.Explore.data.source.remote

import com.alok.groww.Explore.domain.models.Stock
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ExploreApiService {

    @GET("query")
    suspend fun getTopGainersLosers(
        @Query("function") function: String,
        @Query("apikey") apiKey: String
    ): Response<Stock>

    @GET("query")
    suspend fun getCompanyOverview(
        @Query("function") function: String,
        @Query("symbol") symbol: String,
        @Query("apikey") apiKey: String
    ): Response<Stock>

    @GET("query")
    suspend fun getStockPriceHistory(
        @Query("function") function: String,
        @Query("symbol") symbol: String,
        @Query("interval") interval: String,
        @Query("apikey") apiKey: String
    ): Response<Stock>
}