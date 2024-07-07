package com.alok.groww

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.alok.groww.Core.domain.ServerResponse
import com.alok.groww.Core.utils.DepthPageTransformer
import com.alok.groww.Core.utils.ExtensionsUtil.gone
import com.alok.groww.Core.utils.ExtensionsUtil.visible
import com.alok.groww.Explore.domain.models.StockOverviewData
import com.alok.groww.Explore.domain.models.StocksData
import com.alok.groww.Explore.presentation.TrendPageFragment
import com.alok.groww.Explore.presentation.adapters.ViewPagerAdapter
import com.alok.groww.Search.presentation.SearchActivity
import com.alok.groww.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {


    companion object{
        const val TAG = "MainActivity"
    }

    private val binding : ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }


    private val viewModel : MainViewModel by viewModels()
    private lateinit var viewPagerAdapter: ViewPagerAdapter


    private val tabIcons = intArrayOf(
        R.drawable.baseline_trending_up_24,
        R.drawable.baseline_trending_down_24,
        R.drawable.baseline_close_fullscreen_24,
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)


        binding.searchButton.setOnClickListener {
            startActivity(Intent(this, SearchActivity::class.java))
        }

        viewModel.getTrendingStockData()
        setObservers()
    }

    private fun setObservers() {


        viewModel.trendingStockData.observe(this){ response ->

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

                            viewModel.stocksData = response.data
                            setupViews(viewModel.stocksData)
                        }
                    }
                }
        }
    }

    private fun isvalidData(stocksData: StocksData) : Boolean{
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
                binding.layoutMain.visible()

            }
            1->{
                //Loading
                binding.animationViewError.gone()
                binding.animationViewLoading.visible()
                binding.progressParent.visible()
                binding.message.text = getString(R.string.the_stock_market_is_a_device_to_transfer_money_from_the_impatient_to_the_patient)
                binding.layoutMain.gone()

            }
            -1->{
                //Error
                binding.animationViewError.visible()
                binding.animationViewLoading.gone()
                binding.progressParent.visible()
                binding.layoutMain.gone()

            }
        }
    }

    private fun setupViews(stocksData: StocksData) {


        viewPagerAdapter = ViewPagerAdapter(this, stocksData.top_gainers, stocksData.top_losers, stocksData.most_actively_traded)
        binding.viewPager.offscreenPageLimit = 3
        binding.viewPager.setPageTransformer(DepthPageTransformer())
        binding.viewPager.adapter = viewPagerAdapter


        // Setup Tabs
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Top Gainers"
                1 -> "Top Losers"
                2 -> "Most Traded"
                else -> null
            }

            tab.setIcon(tabIcons[position])
        }.attach()

    }



}