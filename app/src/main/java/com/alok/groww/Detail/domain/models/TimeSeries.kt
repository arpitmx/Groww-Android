package com.alok.groww.Detail.domain.models

import com.google.gson.annotations.SerializedName

data class TimeSeries (
    @SerializedName("Meta Data") val metaData: Map<String, String>,
    @SerializedName("Time Series (Daily)") val timeSeriesDaily: Map<String, Series>
)


data class Series(
    @SerializedName("1. open") val open: String,
    @SerializedName("2. high") val high: String,
    @SerializedName("3. low") val low: String,
    @SerializedName("4. close") val close: String,
    @SerializedName("5. volume") val volume: String
)