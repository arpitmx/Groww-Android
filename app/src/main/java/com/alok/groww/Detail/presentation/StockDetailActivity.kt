package com.alok.groww.Detail.presentation

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.alok.groww.Core.domain.ServerResponse
import com.alok.groww.Core.utils.Constants
import com.alok.groww.Core.utils.Convertors
import com.alok.groww.Core.utils.Convertors.getFormattedMarketCap
import com.alok.groww.Core.utils.ExtensionsUtil.gone
import com.alok.groww.Core.utils.ExtensionsUtil.visible
import com.alok.groww.Core.utils.RandomGenerator
import com.alok.groww.Detail.presentation.adapters.GraphAdapter
import com.alok.groww.Explore.domain.models.Stock
import com.alok.groww.Explore.domain.models.StockOverviewData
import com.alok.groww.R
import com.alok.groww.databinding.ActivityStockDetailBinding
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.robinhood.spark.SparkView
import com.robinhood.spark.SparkView.OnScrubListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@AndroidEntryPoint
class StockDetailActivity : AppCompatActivity() {

    private lateinit var stockBundle : String
    private val viewmodel : StockDetailViewModel by viewModels()

    private val binding : ActivityStockDetailBinding by lazy {
        ActivityStockDetailBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setLoader(1)
        val bundle: Bundle? = intent.extras
        if (bundle?.getString(Constants.Keys.BUNDLE_STOCK_DATA) != null) {
            stockBundle = bundle.getString(Constants.Keys.BUNDLE_STOCK_DATA)!!
        }else{
            setLoader(-1)
            return
        }

        stock = parseBundle(stockBundle)
        viewmodel.fetchTimeSeriesAndApplyFilter(stock.ticker)
        viewmodel.getStockOverviewData(stock.ticker)

        setObservers()
    }

    private fun setObservers() {

        viewmodel.stockOverviewLiveData.observe(this){ response ->

            when(response){
                is ServerResponse.Failure -> {
                    setLoader(-1)
                    binding.message.text = response.error ?: "An error occurred"
                    Snackbar.make(binding.root, response.error ?: "An error occurred", Snackbar.LENGTH_LONG).show()
                }
                ServerResponse.Loading -> {
                    setLoader(1)
                }
                is ServerResponse.Success -> {
                    setLoader(0)
                    if (isvalidData(response.data)) {
                        viewmodel.stockOverview = response.data
                        setViews(viewmodel.stockOverview)
                    }
                }
            }


            viewmodel.closingValues.observe(this){ closingValuesResponse ->
                when(closingValuesResponse){
                    is ServerResponse.Failure -> {
                        Snackbar.make(binding.root, closingValuesResponse.error ?: "Failure loading graph data", Snackbar.LENGTH_LONG).show()
                    }
                    ServerResponse.Loading -> {
                        binding.graphLoading.visible()
                    }
                    is ServerResponse.Success -> {
                        binding.graphLoading.gone()
                        setGraph(closingValuesResponse.data)
                    }
                }
            }



        }

    }

    private fun isvalidData(stocksData: StockOverviewData) : Boolean{
        if (stocksData.Information!=null){
            if(stocksData.Information.contains("limit")){
                setLoader(-1)
                binding.message.text = "Halt sparky: Api limit has reached.."
            }
            return false
        }

        return true
    }

    private fun setLoader(state : Int){
        when (state){
            0->{
                //Success
                binding.progressParent.gone()
                binding.animationViewLoading.visible()
                binding.animationViewError.gone()
                binding.message.text = getString(R.string.the_stock_market_is_a_device_to_transfer_money_from_the_impatient_to_the_patient)
                binding.parentMain.visible()

            }
            1->{
                //Loading
                binding.animationViewError.gone()
                binding.animationViewLoading.visible()
                binding.progressParent.visible()
                binding.message.text = getString(R.string.the_stock_market_is_a_device_to_transfer_money_from_the_impatient_to_the_patient)
                binding.parentMain.gone()

            }
            -1->{
                //Error
                binding.animationViewError.visible()
                binding.animationViewLoading.gone()
                binding.progressParent.visible()
                binding.parentMain.gone()

            }
        }
    }


    private fun setGraph(closingValues : List<Float>){

        sparkView = binding.graph

        CoroutineScope(Dispatchers.IO).launch{
            sparkView.adapter = GraphAdapter(closingValues)
        }

        sparkView.scrubListener = OnScrubListener { value ->

            if(value ==null){
                binding.stockPrice.text = "$122.4"
            }else{
                binding.stockPrice.setText("$${String.format("%.2f", value)}")
            }
        }

        sparkView.visible()

    }

    lateinit var stock : Stock
    lateinit var sparkView: SparkView

    private fun parseBundle(jsonStock : String):Stock{
        val gson = Gson()
        val stock = gson.fromJson(jsonStock, Stock::class.java)
        return stock
    }


    private fun setViews(stockOverview: StockOverviewData) {



        //Setting basic values
        binding.actionBarTitle.text = stock.ticker
        binding.stockPrice.text = "$${stock.price}"
        binding.stockPriceChange.text = stock.change_percentage
        binding.currentPriceTv.text = "$${stock.price}"
        binding.stockName.text = stockOverview.Name
        binding.stockSymbolAndType.text = "${stock.ticker}, ${stockOverview.AssetType}\n${stockOverview.Exchange}"


        //Setting Fundamentals

        if (stockOverview.MarketCapitalization=="None"){
            binding.marketCapTv.text = "N/A"
        }else{
            binding.marketCapTv.text = "$${stockOverview.MarketCapitalization.getFormattedMarketCap()}"
        }

        binding.low52week.text = "$${stockOverview.`52WeekLow`}"
        binding.high52week.text = "$${stockOverview.`52WeekHigh`}"
        binding.tvPeRatio.text = stockOverview.PERatio
        binding.tvBetaValue.text = stockOverview.Beta
        binding.tvDividendYield.text = stockOverview.DividendYield
        binding.tvProfitMargin.text = stockOverview.ProfitMargin
        binding.tvPBRatio.text = stockOverview.PriceToBookRatio
        binding.stockDescription.text = stockOverview.Description
        binding.industry.text = stockOverview.Industry
        binding.sector.text = stockOverview.Sector
        binding.aboutStockHeader.text = stockOverview.Name



    }
}