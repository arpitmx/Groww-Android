package com.alok.groww.Explore.data.repositoryImpl

import com.alok.groww.Core.domain.ServerResponse
import com.alok.groww.Core.utils.Constants
import com.alok.groww.Explore.domain.models.StocksData
import com.alok.groww.Explore.domain.repository.StocksRepository
import retrofit2.HttpException
import retrofit2.Response

class StocksRepositoryImpl () : StocksRepository {
        override suspend fun getTopStocksData(): ServerResponse<StocksData> {
            return try {
                //val response = RetrofitInstance.exploreApi.getTopGainersLosers()
                val response : Response<StocksData> = Response.success(Constants.TrendTest.getTrendData())
                if (response.isSuccessful) {
                    response.body()?.let {
                        ServerResponse.Success(it)
                    } ?: ServerResponse.Failure("No data available")
                } else {
                    ServerResponse.Failure(HttpException(response).message())
                }
            } catch (e: Exception) {
                ServerResponse.Failure(e.message.toString())
            }
        }


}

