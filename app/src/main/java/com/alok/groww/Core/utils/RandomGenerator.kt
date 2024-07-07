package com.alok.groww.Core.utils

import kotlin.random.Random

object RandomGenerator {

    object Chart{
        fun generateRandomStockPrices(count: Int, minPrice: Float = 1.0f, maxPrice: Float = 1000.0f): List<Float> {
            val stockPrices = mutableListOf<Float>()
            for (i in 0 until count) {
                val price = Random.nextFloat() * (maxPrice - minPrice) + minPrice
                stockPrices.add(price)
            }
            return stockPrices
        }
    }

}