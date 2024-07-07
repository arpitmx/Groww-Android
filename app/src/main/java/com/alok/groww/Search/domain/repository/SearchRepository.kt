package com.alok.groww.Search.domain.repository

import com.alok.groww.Core.domain.ServerResponse
import com.alok.groww.Explore.domain.models.StockOverviewData
import com.alok.groww.Explore.domain.models.StocksData
import com.alok.groww.Search.domain.models.SearchItem
import com.alok.groww.Search.domain.models.SearchResponse

interface SearchRepository {

    suspend fun getSearchResults(query: String): ServerResponse<SearchResponse>

}
