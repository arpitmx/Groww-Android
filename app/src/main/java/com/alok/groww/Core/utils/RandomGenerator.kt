package com.alok.groww.Core.utils

import com.alok.groww.Search.domain.models.SearchItem
import com.alok.groww.Search.domain.models.SearchResponse
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

    object Search{


        // Generate random test data
        fun generateTestData(keyword: String, count: Int): SearchResponse {
            val bestMatches = mutableListOf<SearchItem>()
            val types = listOf("Equity","ETF","Mutual Fund")
            repeat(count) {
                val symbol = generateRandomSymbol()
                val name = generateRandomName(keyword)
                bestMatches.add(
                    SearchItem(
                        symbol = "AAPL",
                        name = name,
                        type = types.get(Random.nextInt(0,3)),
                        region = "United Kingdom",
                        marketOpen = "08:00",
                        marketClose = "16:30",
                        timezone = "UTC+01",
                        currency = "GBX",
                        matchScore = "0.${Random.nextInt(500, 999)}"
                    )
                )
            }

            return SearchResponse(
                Information = null,
                bestMatches = bestMatches
            )
        }

        // Generate a random symbol
        fun generateRandomSymbol(): String {
            val chars = ('A'..'Z') + ('0'..'9')
            return (1..6)
                .map { chars.random() }
                .joinToString("")
        }

        // Generate a random name including the keyword
        fun generateRandomName(keyword: String): String {
            val words = listOf("Corporation", "Limited", "Group", "Company", "Holdings", "Industries", "Technologies")
            val randomWord = words.random()
            return "$keyword $randomWord"
        }

    }

}