package com.alok.groww.Explore.data.source.remote

import com.alok.groww.BuildConfig
import com.alok.groww.Explore.domain.models.Stock
import com.alok.groww.Explore.domain.models.StockOverviewData
import com.alok.groww.Explore.domain.models.StocksData
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ExploreApiService {

    @GET("/query?function=TOP_GAINERS_LOSERS&apikey=${BuildConfig.API_KEY}")
    suspend fun getTopGainersLosers(): Response<StocksData>

}