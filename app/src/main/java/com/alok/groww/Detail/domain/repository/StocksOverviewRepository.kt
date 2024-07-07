package com.alok.groww.Explore.domain.repository

import com.alok.groww.Core.domain.ServerResponse
import com.alok.groww.Detail.domain.models.TimeSeries
import com.alok.groww.Explore.domain.models.StockOverviewData

interface StocksOverviewRepository {

    suspend fun getStockOverview(symbol : String): ServerResponse<StockOverviewData>
    suspend fun getStockTimeSeriesData(symbol : String): ServerResponse<TimeSeries>

}
