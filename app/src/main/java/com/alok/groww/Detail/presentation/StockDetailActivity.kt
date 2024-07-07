package com.alok.groww.Detail.presentation

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import android.window.OnBackInvokedDispatcher
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.lifecycle.Observer
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
import com.alok.groww.Search.presentation.SearchActivity
import com.alok.groww.databinding.ActivityStockDetailBinding
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.robinhood.spark.SparkView
import com.robinhood.spark.SparkView.OnScrubListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Locale
import java.util.Random


@AndroidEntryPoint
class StockDetailActivity : AppCompatActivity() {

    private lateinit var stockBundle: String
    private val viewmodel: StockDetailViewModel by viewModels()

    private val binding: ActivityStockDetailBinding by lazy {
        ActivityStockDetailBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setLoader(1)
        val bundle: Bundle? = intent.extras
        if (bundle?.getString(Constants.Keys.BUNDLE_STOCK_DATA) != null) {
            stockBundle = bundle.getString(Constants.Keys.BUNDLE_STOCK_DATA)!!
            viewmodel.stock = parseBundle(stockBundle)

            if (viewmodel.stock.volume == Constants.Raw.PASSED_FROM_SEARCH){

                viewmodel.fetchTimeSeriesAndApplyFilter(viewmodel.stock.ticker)
                viewmodel.getStockOverviewData(viewmodel.stock.ticker)
                viewmodel.getStockDetails(viewmodel.stock.ticker)
                setLoader(0)
                
            }else{
                viewmodel.fetchTimeSeriesAndApplyFilter(viewmodel.stock.ticker)
                viewmodel.getStockOverviewData(viewmodel.stock.ticker)

                binding.stockPrice.text = "$${viewmodel.stock.price}"
                binding.stockPriceChange.text = viewmodel.stock.change_percentage
                binding.currentPriceTv.text = "$${viewmodel.stock.price}"
                setLoader(0)

            }
           

        } else {
            setLoader(-1)
            return
        }

        setupCircularSymbolView(viewmodel.stock.ticker)
        setListners()
        setObservers()



    }

    private fun setListners() {

        binding.backButton.setOnClickListener{
            onBackPressed()
        }

        binding.searchButton.setOnClickListener{
            startActivity(Intent(this,SearchActivity::class.java))
        }

    }

    private fun setObservers() {

        viewmodel.stockOverviewLiveData.observe(this) { response ->

            when (response) {
                is ServerResponse.Failure -> {
                    setLoader(-1)
                    binding.message.text = response.error ?: "An error occurred"
                    Snackbar.make(
                        binding.root,
                        response.error ?: "An error occurred",
                        Snackbar.LENGTH_LONG
                    ).show()
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


            viewmodel.closingValues.observe(this) { closingValuesResponse ->
                when (closingValuesResponse) {
                    is ServerResponse.Failure -> {
                        binding.tabs.gone()
                        Snackbar.make(
                            binding.root,
                            closingValuesResponse.error ?: "Failure loading graph data",
                            Snackbar.LENGTH_LONG
                        ).show()
                    }

                    ServerResponse.Loading -> {
                        binding.tabs.gone()
                        binding.graphLoading.visible()
                    }

                    is ServerResponse.Success -> {
                        binding.graphLoading.gone()
                        setGraph(closingValuesResponse.data)
                    }
                }
            }


            viewmodel.selectedClosingValues.observe(this) { closingValuesResponse ->
                when (closingValuesResponse) {
                    is ServerResponse.Failure -> {
                        binding.tabs.gone()
                        binding.graph.gone()
                        binding.graphLoading.visible()
                        binding.graphLoading.playAnimation()
                        Snackbar.make(
                            binding.root,
                            closingValuesResponse.error ?: "Failure loading graph data",
                            Snackbar.LENGTH_LONG
                        ).show()
                    }

                    ServerResponse.Loading -> {
                        binding.tabs.gone()
                        binding.graphLoading.visible()
                        binding.graphLoading.playAnimation()
                        binding.graph.gone()
                    }

                    is ServerResponse.Success -> {
                        binding.graphLoading.gone()
                        binding.graphLoading.cancelAnimation()
                        binding.tabs.visible()
                        binding.graph.visible()


                        graphAdapter.updateYData(closingValuesResponse.data)


                    }
                }
            }
            
            
            viewmodel.stockDetailsLiveData.observe(this){ response ->
                
                when(response){
                    is ServerResponse.Failure -> {

                        setLoader(-1)
                        Snackbar.make(
                            binding.root,
                            response.error ?: "Failure loading stock data",
                            Snackbar.LENGTH_LONG
                        ).show()
                        
                    }
                    ServerResponse.Loading -> {
                        
                        setLoader(0)
                    }
                    is ServerResponse.Success -> {
                        
                        setLoader(0)
                        val r = response.data.globalQuote
                        val stock = Stock(price = r.price, ticker = r.symbol, change_amount = r.change, change_percentage = r.changePercent, volume = r.volume  )
                        viewmodel.stock = stock

                        val price = viewmodel.stock.price.toDoubleOrNull()
                        if (price != null) {
                            binding.stockPrice.text = "$${String.format("%.2f", price)}"
                            binding.currentPriceTv.text = "$${String.format("%.2f", price)}"

                        } else {
                            binding.stockPrice.text = "N/A"
                        }

                        binding.stockPriceChange.text = viewmodel.stock.change_percentage

                    }
                }
                
            }


        }

    }

    override fun onBackPressed() {
        super.onBackPressed()
        this.finish()
    }


    private fun isvalidData(stocksData: StockOverviewData): Boolean {
        if (stocksData.Information != null) {
            if (stocksData.Information.contains("limit")) {
                setLoader(-1)
                binding.message.text = "Halt sparky: Api limit has reached.."
            }
            return false
        }

        return true
    }

    private fun setLoader(state: Int) {
        when (state) {
            0 -> {
                //Success
                binding.progressParent.gone()
                binding.animationViewLoading.visible()
                binding.animationViewError.gone()
                binding.message.text =
                    getString(R.string.the_stock_market_is_a_device_to_transfer_money_from_the_impatient_to_the_patient)
                binding.parentMain.visible()

            }

            1 -> {
                //Loading
                binding.animationViewError.gone()
                binding.animationViewLoading.visible()
                binding.progressParent.visible()
                binding.message.text =
                    getString(R.string.the_stock_market_is_a_device_to_transfer_money_from_the_impatient_to_the_patient)
                binding.parentMain.gone()

            }

            -1 -> {
                //Error
                binding.animationViewError.visible()
                binding.animationViewLoading.gone()
                binding.progressParent.visible()
                binding.parentMain.gone()

            }
        }
    }


    lateinit var graphAdapter: GraphAdapter
    private fun setGraph(closingValues: List<Float>) {

        sparkView = binding.graph

        CoroutineScope(Dispatchers.IO).launch {
            graphAdapter = GraphAdapter(closingValues.toMutableList())
            sparkView.adapter = graphAdapter
        }

        sparkView.scrubListener = OnScrubListener { value ->

            if (value == null) {
                binding.stockPrice.text = "$${viewmodel.stock.price}"
            } else {
                binding.stockPrice.setText("$${String.format("%.2f", value)}")
            }
        }

        sparkView.visible()
        setTabs()

    }

    private fun setTabs() {


        val tabWeek = binding.tabWeek
        val tabMonth = binding.tabMonth
        val tabYear = binding.tabYear
        val tabAll = binding.tabAll


        viewmodel.selectedTab.observe(this, Observer { tab ->
            when (tab) {
                StockDetailViewModel.Tab.WEEK -> {

                    selectTab(tabWeek)
                    deselectTab(tabMonth, tabYear, tabAll)
                }

                StockDetailViewModel.Tab.MONTH -> {

                    selectTab(tabMonth)
                    deselectTab(tabWeek, tabYear, tabAll)
                }

                StockDetailViewModel.Tab.YEAR -> {

                    selectTab(tabYear)
                    deselectTab(tabWeek, tabMonth, tabAll)
                }

                StockDetailViewModel.Tab.ALL -> {

                    selectTab(tabAll)
                    deselectTab(tabWeek, tabMonth, tabYear)
                }
            }
        })

        tabWeek.setOnClickListener {
            if (viewmodel.selectedTab.value != StockDetailViewModel.Tab.WEEK){

                viewmodel.setClosingValues(Constants.Timeseries.WEEK)
                viewmodel.selectTab(StockDetailViewModel.Tab.WEEK)
            }
        }
        tabMonth.setOnClickListener {

            if (viewmodel.selectedTab.value != StockDetailViewModel.Tab.MONTH) {
                viewmodel.setClosingValues(Constants.Timeseries.MONTH)
                viewmodel.selectTab(StockDetailViewModel.Tab.MONTH)
            }
        }
        tabYear.setOnClickListener {

            if (viewmodel.selectedTab.value != StockDetailViewModel.Tab.YEAR) {
                viewmodel.setClosingValues(Constants.Timeseries.YEAR)
                viewmodel.selectTab(StockDetailViewModel.Tab.YEAR)
            }
        }
        tabAll.setOnClickListener {
            if (viewmodel.selectedTab.value != StockDetailViewModel.Tab.ALL) {
                viewmodel.setClosingValues(Constants.Timeseries.ALL)
                viewmodel.selectTab(StockDetailViewModel.Tab.ALL)
            }
        }

        binding.tabs.visible()

    }

    private fun selectTab(selectedButton: AppCompatButton) {
        selectedButton.setBackgroundResource(R.drawable.rounded_bg_details_selected)
    }

    private fun deselectTab(vararg buttons: AppCompatButton) {
        for (button in buttons) {
            button.setBackgroundResource(R.drawable.rounded_bg_details)
        }
    }

    
    lateinit var sparkView: SparkView

    private fun parseBundle(jsonStock: String): Stock {
        val gson = Gson()
        val stock = gson.fromJson(jsonStock, Stock::class.java)
        return stock
    }


    private fun setViews(stockOverview: StockOverviewData) {


        //Setting basic values
        binding.actionBarTitle.text = viewmodel.stock.ticker
        binding.stockName.text = stockOverview.Name
        binding.stockSymbolAndType.text =
            "${viewmodel.stock.ticker}, ${stockOverview.AssetType}\n${stockOverview.Exchange}"


        //Setting Fundamentals

        if (stockOverview.MarketCapitalization == "None") {
            binding.marketCapTv.text = "N/A"
        } else {
            binding.marketCapTv.text =
                "$${stockOverview.MarketCapitalization.getFormattedMarketCap()}"
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



//        val AnalystRatingHold: String,
        // Analysis
        with(binding){

            targetPrice.text = stockOverview.AnalystTargetPrice
            strongBuy.text = stockOverview.AnalystRatingStrongBuy
            ratingBuy.text = stockOverview.AnalystRatingBuy
            ratingSell.text = stockOverview.AnalystRatingSell
            strongSell.text = stockOverview.AnalystRatingStrongSell
            strongHold.text = stockOverview.AnalystRatingHold

        }


    }


    fun setupCircularSymbolView( symbol: String) {
        binding.tvSymbol.text = symbol.take(2).uppercase(Locale.getDefault())
        val randomColor = generateRandomColor()
        binding.parentIcon.background.setTint(randomColor)
    }

    private fun generateRandomColor(): Int {
        val rnd = Random()
        return Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))
    }
}