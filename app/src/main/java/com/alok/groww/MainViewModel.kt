package com.alok.groww

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alok.groww.Core.domain.ServerResponse
import com.alok.groww.Explore.domain.models.StockOverviewData
import com.alok.groww.Explore.domain.models.StocksData
import com.alok.groww.Explore.domain.repository.StocksRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(val repository : StocksRepository): ViewModel() {

    companion object{
        const val TAG = "MainViewModel"
    }
    lateinit var stocksData: StocksData

    private val _trendingStockData = MutableLiveData<ServerResponse<StocksData>>(ServerResponse.Loading)
    val trendingStockData: LiveData<ServerResponse<StocksData>> get() = _trendingStockData




        fun getTrendingStockData() {

            viewModelScope.launch(Dispatchers.IO) {
                try {
                    val result = repository.getTopStocksData()

                    when(result){
                        is ServerResponse.Failure -> {
                            _trendingStockData.postValue(ServerResponse.Failure(result.error))
                        }
                        ServerResponse.Loading -> {
                            _trendingStockData.postValue(ServerResponse.Loading)
                        }
                        is ServerResponse.Success -> {
                            _trendingStockData.postValue(ServerResponse.Success(result.data))
                        }
                    }



                } catch (e: Exception) {
                    _trendingStockData.postValue(ServerResponse.Failure(e.toString()))
                }
            }
        }


}