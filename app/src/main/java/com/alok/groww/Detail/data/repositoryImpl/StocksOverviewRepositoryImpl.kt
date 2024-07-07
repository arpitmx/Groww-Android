package com.alok.groww.Explore.data.repositoryImpl

import com.alok.groww.Core.data.RetrofitInstance
import com.alok.groww.Core.domain.ServerResponse
import com.alok.groww.Core.utils.Constants
import com.alok.groww.Core.utils.Convertors
import com.alok.groww.Detail.domain.models.TimeSeries
import com.alok.groww.Explore.domain.models.StockOverviewData
import com.alok.groww.Explore.domain.repository.StocksOverviewRepository
import kotlinx.coroutines.Delay
import kotlinx.coroutines.delay

import retrofit2.HttpException
import retrofit2.Response

class StocksOverviewRepositoryImpl () : StocksOverviewRepository {
    override suspend fun getStockOverview(symbol : String): ServerResponse<StockOverviewData> {
            return try {
                lateinit var response : Response<StockOverviewData>
                if (!Constants.isTestMode){
                    response = RetrofitInstance.overviewApi.getStockOverview(symbol)
                }else{
                    delay(2000)
                    response = Response.success(Constants.OverviewTest.getOverviewData())
                }

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

    override suspend fun getStockTimeSeriesData(symbol: String): ServerResponse<TimeSeries> {
        return try {
            lateinit var response : Response<TimeSeries>
            if (!Constants.isTestMode){
                response = RetrofitInstance.overviewApi.getStockTimeSeriesData(symbol)
            }else{
                delay(2000)
                response = Response.success(Convertors.getTimeSeriesObject(Convertors.jsonString))
            }

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

