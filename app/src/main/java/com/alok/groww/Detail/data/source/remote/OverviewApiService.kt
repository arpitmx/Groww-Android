package com.alok.groww.Explore.data.source.remote

import com.alok.groww.BuildConfig
import com.alok.groww.Detail.domain.models.TimeSeries
import com.alok.groww.Explore.domain.models.StockOverviewData
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface OverviewApiService {

    // Retrofit service interface

    @GET("/query?function=OVERVIEW&apikey=${BuildConfig.API_KEY}")
    suspend fun getStockOverview(@Query("symbol") symbol: String): Response<StockOverviewData>

    @GET("/query?function=TIME_SERIES_DAILY&outputsize=full&apikey=${BuildConfig.API_KEY}")
    suspend fun getStockTimeSeriesData(@Query("symbol") symbol: String): Response<TimeSeries>


}