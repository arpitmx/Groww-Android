package com.alok.groww.Detail.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alok.groww.Core.domain.ServerResponse
import com.alok.groww.Core.utils.Constants
import com.alok.groww.Core.utils.Convertors
import com.alok.groww.Detail.domain.models.TimeSeries
import com.alok.groww.Explore.domain.models.StockOverviewData
import com.alok.groww.Explore.domain.repository.StocksOverviewRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


@HiltViewModel
class StockDetailViewModel @Inject constructor(val repository: StocksOverviewRepository) : ViewModel(){

    lateinit var ticker : String
    var selected_timeseries : String = Constants.Timeseries.ALL
    lateinit var timeSeries: TimeSeries
    lateinit var closeingValues : List<Float>


    companion object{
        const val TAG = "MainViewModel"
    }
    lateinit var stockOverview: StockOverviewData

    private val _stockOverviewLiveData = MutableLiveData<ServerResponse<StockOverviewData>>(ServerResponse.Loading)
    val stockOverviewLiveData: LiveData<ServerResponse<StockOverviewData>> get() = _stockOverviewLiveData

    private val _closingValues = MutableLiveData<ServerResponse<List<Float>>>()
    val closingValues: LiveData<ServerResponse<List<Float>>> get() = _closingValues

    private val _timeSeriesLiveData = MutableLiveData<ServerResponse<TimeSeries>>()
    val timeSeriesLiveData: LiveData<ServerResponse<TimeSeries>> get() = _timeSeriesLiveData




    fun fetchTimeSeriesAndApplyFilter(ticker: String) {


        _closingValues.value = ServerResponse.Loading
        viewModelScope.launch {
            try {
                // First task: Downloading Timeseries Data
                val timeSeries = withContext(Dispatchers.IO) {
                    repository.getStockTimeSeriesData(ticker)
                }

                // Second task: Applying default filter to timeseries (1W)
                when(timeSeries){
                    is ServerResponse.Failure -> {
                        _closingValues.value = ServerResponse.Failure(timeSeries.error)
                    }
                    ServerResponse.Loading -> {
                        _closingValues.value = ServerResponse.Loading
                    }
                    is ServerResponse.Success -> {
                        val processedData = withContext(Dispatchers.IO) {
                            Convertors.getClosingValues(timeSeries.data,Constants.Timeseries.YEAR)
                        }

                        _closingValues.value = ServerResponse.Success(processedData)
                    }
                }

            } catch (e: Exception) {
                _closingValues.value = ServerResponse.Failure(e.message ?: "Unknown error")
            }
        }
    }



//    fun getClosingValues(timeSeries: String) {
//        _closingValues.value = ServerResponse.Loading
//        viewModelScope.launch {
//            try {
//                val result = withContext(Dispatchers.IO) {
//                    Convertors.getClosingValues(Convertors.jsonString, timeSeries)
//                }
//                _closingValues.value = ServerResponse.Success(result)
//            } catch (e: Exception) {
//                _closingValues.value = ServerResponse.Failure(e.message ?: "Unknown error")
//            }
//        }
//    }

    fun getStockTimeSeriesData(ticker: String) {

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = repository.getStockTimeSeriesData(ticker)

                when(result){
                    is ServerResponse.Failure -> {
                        _timeSeriesLiveData.postValue(ServerResponse.Failure(result.error))
                    }
                    ServerResponse.Loading -> {
                        _timeSeriesLiveData.postValue(ServerResponse.Loading)
                    }
                    is ServerResponse.Success -> {
                        _timeSeriesLiveData.postValue(ServerResponse.Success(result.data))
                    }
                }

            } catch (e: Exception) {
                _stockOverviewLiveData.postValue(ServerResponse.Failure(e.toString()))
            }
        }
    }






    fun getStockOverviewData(ticker: String) {

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = repository.getStockOverview(ticker)

                when(result){
                    is ServerResponse.Failure -> {
                        _stockOverviewLiveData.postValue(ServerResponse.Failure(result.error))
                    }
                    ServerResponse.Loading -> {
                        _stockOverviewLiveData.postValue(ServerResponse.Loading)
                    }
                    is ServerResponse.Success -> {
                        _stockOverviewLiveData.postValue(ServerResponse.Success(result.data))
                    }
                }

            } catch (e: Exception) {
                _stockOverviewLiveData.postValue(ServerResponse.Failure(e.toString()))
            }
        }



    }

}