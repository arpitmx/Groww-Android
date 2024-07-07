package com.alok.groww.Explore.data.source.remote

import com.alok.groww.BuildConfig
import com.alok.groww.Explore.domain.models.StockOverviewData
import com.alok.groww.Explore.domain.models.StocksData
import com.alok.groww.Search.domain.models.SearchItem
import com.alok.groww.Search.domain.models.SearchResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface SearchApiService {

    @GET("/query?function=SYMBOL_SEARCH&apikey=${BuildConfig.API_KEY}")
    suspend fun searchKeyword(@Query("keywords") keywords: String): Response<SearchResponse>




}