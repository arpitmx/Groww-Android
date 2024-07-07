package com.alok.groww.Explore.domain.repository

import com.alok.groww.Core.domain.ServerResponse
import com.alok.groww.Explore.domain.models.StockOverviewData
import com.alok.groww.Explore.domain.models.StocksData

interface StocksRepository {

    suspend fun getTopStocksData(): ServerResponse<StocksData>

}
