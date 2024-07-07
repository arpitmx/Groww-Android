package com.alok.groww.Explore.domain.models

import androidx.room.Entity



data class StocksData(
    val Information: String?,
    val metadata: String,
    val last_updated: String,
    val top_gainers: List<Stock>,
    val top_losers: List<Stock>,
    val most_actively_traded : List<Stock>
)