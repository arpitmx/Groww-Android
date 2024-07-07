package com.alok.groww.Explore.data.repositoryImpl

import com.alok.groww.Core.data.RetrofitInstance
import com.alok.groww.Core.domain.ServerResponse
import com.alok.groww.Core.utils.Constants
import com.alok.groww.Core.utils.RandomGenerator
import com.alok.groww.Explore.domain.models.StocksData
import com.alok.groww.Explore.domain.repository.StocksRepository
import com.alok.groww.Search.domain.models.SearchItem
import com.alok.groww.Search.domain.models.SearchResponse
import com.alok.groww.Search.domain.repository.SearchRepository
import kotlinx.coroutines.delay
import retrofit2.HttpException
import retrofit2.Response
import kotlin.random.Random

class SearchRepositoryImpl() : SearchRepository {

    override suspend fun getSearchResults(keyword: String): ServerResponse<SearchResponse> {
        return try {
            lateinit var response : Response<SearchResponse>
            if (!Constants.isTestMode){
                response = RetrofitInstance.searchApi.searchKeyword(keyword)
            }else{
                delay(1000)
                response = Response.success(RandomGenerator.Search.generateTestData(keyword, Random.nextInt(3,21)))
            }

            if (response.isSuccessful) {
                response.body()?.let {
                    if (response.body()?.Information!=null){
                        ServerResponse.Failure("Api limit exceeded")
                    }else{
                        ServerResponse.Success(it)
                    }
                } ?: ServerResponse.Failure("No data available")
            } else {
                ServerResponse.Failure(HttpException(response).message())
            }
        } catch (e: Exception) {
            ServerResponse.Failure(e.message.toString())
        }
    }


}

