package com.alok.groww.Detail.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alok.groww.Core.domain.ServerResponse
import com.alok.groww.Core.utils.Constants
import com.alok.groww.Core.utils.Convertors
import com.alok.groww.Detail.domain.models.StockDetails
import com.alok.groww.Detail.domain.models.TimeSeries
import com.alok.groww.Explore.domain.models.Stock
import com.alok.groww.Explore.domain.models.StockOverviewData
import com.alok.groww.Explore.domain.repository.StocksOverviewRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject


@HiltViewModel
class StockDetailViewModel @Inject constructor(val repository: StocksOverviewRepository) :
    ViewModel() {

    lateinit var ticker: String
    lateinit var stock : Stock
    var selected_timeseries: String = Constants.Timeseries.YEAR
    lateinit var timeSeriesData: TimeSeries
    var closingValuesWeek: List<Float>? = null
    var closingValuesMonth: List<Float>? = null
    var closingValuesYear: List<Float>? = null
    var closingValuesAll: List<Float>? = null


    companion object {
        const val TAG = "MainViewModel"
    }

    lateinit var stockOverview: StockOverviewData

    private val _stockOverviewLiveData =
        MutableLiveData<ServerResponse<StockOverviewData>>(ServerResponse.Loading)
    val stockOverviewLiveData: LiveData<ServerResponse<StockOverviewData>> get() = _stockOverviewLiveData

    private val _closingValues = MutableLiveData<ServerResponse<List<Float>>>()
    val closingValues: LiveData<ServerResponse<List<Float>>> get() = _closingValues

    private val _selectedClosingValues = MutableLiveData<ServerResponse<List<Float>>>()
    val selectedClosingValues: LiveData<ServerResponse<List<Float>>> get() = _selectedClosingValues

    private val _timeSeriesLiveData = MutableLiveData<ServerResponse<TimeSeries>>()
    val timeSeriesLiveData: LiveData<ServerResponse<TimeSeries>> get() = _timeSeriesLiveData

    private val _stockDetailsLiveData =
        MutableLiveData<ServerResponse<StockDetails>>(ServerResponse.Loading)
    val stockDetailsLiveData: LiveData<ServerResponse<StockDetails>> get() = _stockDetailsLiveData


    enum class Tab {
        WEEK, MONTH, YEAR, ALL
    }


    private val _selectedTab = MutableLiveData<Tab>().apply { value = Tab.YEAR }
    val selectedTab: LiveData<Tab> get() = _selectedTab

    fun selectTab(tab: Tab) {
        _selectedTab.value = tab
    }

    fun getStockDetails(ticker: String) {
        _stockDetailsLiveData.postValue(ServerResponse.Loading)

        viewModelScope.launch {
            try {

                val response = repository.getStockGlobalData(ticker)
                when(response){
                    is ServerResponse.Failure -> {
                        _stockDetailsLiveData.postValue(ServerResponse.Failure(response.error))
                    }
                    ServerResponse.Loading -> {
                        _stockDetailsLiveData.postValue(ServerResponse.Loading)
                    }
                    is ServerResponse.Success -> {
                        _stockDetailsLiveData.postValue(ServerResponse.Success(response.data))
                    }
                }

            } catch (e: Exception) {
                _selectedClosingValues.value = ServerResponse.Failure(e.message ?: "Unknown error")
            }
        }
    }



fun fetchTimeSeriesAndApplyFilter(ticker: String) {


    _closingValues.value = ServerResponse.Loading
    viewModelScope.launch {
        try {
            // First task: Downloading Timeseries Data
            val timeSeries = withContext(Dispatchers.IO) {
                repository.getStockTimeSeriesData(ticker)
            }

            // Second task: Applying default filter to timeseries (1W)
            when (timeSeries) {
                is ServerResponse.Failure -> {
                    _closingValues.value = ServerResponse.Failure(timeSeries.error)
                }

                ServerResponse.Loading -> {
                    _closingValues.value = ServerResponse.Loading
                }

                is ServerResponse.Success -> {


                    timeSeriesData = timeSeries.data
                    val processedData = withContext(Dispatchers.IO) {
                        Convertors.getClosingValues(timeSeriesData, Constants.Timeseries.YEAR)
                    }

                    setCacheTimeSeriesData(processedData, Constants.Timeseries.YEAR)
                    Timber.tag(TAG)
                        .d("Time Series Data: ${getCachedTimeSeriesData(Constants.Timeseries.YEAR)}")
                    _closingValues.value =
                        ServerResponse.Success(getCachedTimeSeriesData(Constants.Timeseries.YEAR)!!)
                }
            }

        } catch (e: Exception) {
            _closingValues.value = ServerResponse.Failure(e.message ?: "Unknown error")
        }
    }
}


//Set Closing Values after initial setup
fun setClosingValues(timeSeriesType: String) {
    _selectedClosingValues.value = ServerResponse.Loading

    if (getCachedTimeSeriesData(timeSeriesType) == null) {
        viewModelScope.launch {
            try {
                val result = withContext(Dispatchers.IO) {
                    Convertors.getClosingValues(timeSeriesData, timeSeriesType)
                }

                setCacheTimeSeriesData(result, timeSeriesType)
                _selectedClosingValues.value =
                    ServerResponse.Success(getCachedTimeSeriesData(timeSeriesType)!!)
            } catch (e: Exception) {
                _selectedClosingValues.value = ServerResponse.Failure(e.message ?: "Unknown error")
            }
        }
    } else {
        _selectedClosingValues.value =
            ServerResponse.Success(getCachedTimeSeriesData(timeSeriesType)!!)
    }

}


fun setCacheTimeSeriesData(data: List<Float>, timeSeriesType: String) {
    when (timeSeriesType) {
        Constants.Timeseries.WEEK -> {
            if (closingValuesWeek == null) {
                closingValuesWeek = data
            }
        }

        Constants.Timeseries.MONTH -> {
            if (closingValuesMonth == null) {
                closingValuesMonth = data
            }
        }

        Constants.Timeseries.YEAR -> {
            if (closingValuesYear == null) {
                closingValuesYear = data
            }
        }

        Constants.Timeseries.ALL -> {
            if (closingValuesAll == null) {
                closingValuesAll = data
            }
        }
    }
}

fun getCachedTimeSeriesData(timeSeriesType: String): List<Float>? {
    return when (timeSeriesType) {
        Constants.Timeseries.WEEK -> {
            closingValuesWeek
        }

        Constants.Timeseries.MONTH -> {
            closingValuesMonth
        }

        Constants.Timeseries.YEAR -> {
            closingValuesYear
        }

        Constants.Timeseries.ALL -> {
            closingValuesAll
        }

        else -> {
            null
        }
    }
}


fun getStockOverviewData(ticker: String) {

    viewModelScope.launch(Dispatchers.IO) {
        try {
            val result = repository.getStockOverview(ticker)

            when (result) {
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