package com.alok.groww.Core.data

import com.alok.groww.Explore.data.source.remote.ExploreApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    val exploreApi: ExploreApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://www.alphavantage.co/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ExploreApiService::class.java)
    }
}