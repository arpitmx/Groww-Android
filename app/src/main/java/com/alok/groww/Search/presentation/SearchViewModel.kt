package com.alok.groww.Search.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.alok.groww.Core.domain.ServerResponse
import com.alok.groww.Search.domain.models.SearchItem
import com.alok.groww.Search.domain.models.SearchResponse
import com.alok.groww.Search.domain.repository.SearchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject


@HiltViewModel
class SearchViewModel @Inject constructor(val repository: SearchRepository) : ViewModel() {


    lateinit var searchReponse : SearchResponse

    private val _searchResults = MutableLiveData<ServerResponse<SearchResponse>>()
    val searchResults: LiveData<ServerResponse<SearchResponse>> get() = _searchResults

    private val searchJob = Job()
    private val searchScope = CoroutineScope(Dispatchers.IO + searchJob)

    fun searchStocks(query: String) {

        searchScope.launch {

            _searchResults.postValue(ServerResponse.Loading)
            val response = fetchSearchResults(query) // Replace with actual network call
            delay(1000) // Simulate network delay

            when (response) {
                is ServerResponse.Failure -> {
                    _searchResults.postValue(ServerResponse.Failure(response.error))
                }

                ServerResponse.Loading -> {
                    _searchResults.postValue(ServerResponse.Loading)
                }

                is ServerResponse.Success -> {
                    _searchResults.postValue(ServerResponse.Success(response.data))

                }
            }

        }
    }

    private suspend fun fetchSearchResults(query: String): ServerResponse<SearchResponse> {

        val response = repository.getSearchResults(query)
        return response
    }

    override fun onCleared() {
        super.onCleared()
        searchJob.cancel()
    }
}
