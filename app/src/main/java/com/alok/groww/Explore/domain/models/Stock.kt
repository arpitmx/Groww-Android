package com.alok.groww.Explore.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class Stock (val ticker : String, val price : String, val change_amount: String, val change_percentage: String, val volume:String)