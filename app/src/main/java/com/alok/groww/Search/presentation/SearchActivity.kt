package com.alok.groww.Search.presentation

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alok.groww.Core.domain.ServerResponse
import com.alok.groww.Core.utils.Constants
import com.alok.groww.Core.utils.ExtensionsUtil.gone
import com.alok.groww.Core.utils.ExtensionsUtil.invisible
import com.alok.groww.Core.utils.ExtensionsUtil.visible
import com.alok.groww.Detail.presentation.StockDetailActivity
import com.alok.groww.Explore.domain.models.Stock
import com.alok.groww.R
import com.alok.groww.Search.presentation.adapters.SearchItemAdapter
import com.alok.groww.databinding.ActivitySearchBinding
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchActivity : AppCompatActivity(), SearchItemAdapter.OnItemClickListener {

    private val searchViewModel: SearchViewModel by viewModels()
    private lateinit var searchItemAdapter: SearchItemAdapter
    private lateinit var searchEditText: EditText
    private val binding: ActivitySearchBinding by lazy { ActivitySearchBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        searchEditText = binding.searchInput

        setUpRecyclerview()
        setupObservers()
        setupSearch()

    }

    private fun setUpRecyclerview(){
        searchItemAdapter = SearchItemAdapter(this)
        binding.recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.recyclerView.adapter = searchItemAdapter
    }


    private fun setupObservers(){
        searchViewModel.searchResults.observe(this) { response ->

            when(response){
                is ServerResponse.Failure -> {
                    binding.progressBar.invisible()
                    binding.placeholder.visible()
                    binding.recyclerView.gone()
                    binding.placeholder.text = response.error?:"Unknown Error"
                    Snackbar.make(binding.root, response.error?:"Unknown Error", Snackbar.LENGTH_LONG).show()
                }
                ServerResponse.Loading -> {
                    binding.progressBar.visible()
                    binding.placeholder.visible()
                    binding.recyclerView.gone()
                    binding.placeholder.text = "Searching..."
                }
                is ServerResponse.Success -> {
                    binding.progressBar.invisible()

                    if (response.data.bestMatches.isEmpty()){
                        binding.placeholder.visible()
                        binding.recyclerView.gone()
                        binding.placeholder.text = "No Stocks found :( .."
                    }else {
                        binding.recyclerView.visible()
                        binding.placeholder.gone()
                        binding.placeholder.text = "Search Your Stocks Sparky..."
                        searchViewModel.searchReponse = response.data
                        searchItemAdapter.submitList(response.data.bestMatches)
                    }
                }
            }

        }
    }

    private fun setupSearch() {

//
//        binding.searchButton.setOnClickListener{
//            searchViewModel.searchStocks(searchEditText.text.toString())
//        }

        binding.backButton.setOnClickListener{
            onBackPressed()
            finish()
        }

        searchEditText.addTextChangedListener(object : TextWatcher {
            private var searchJob: Job? = null

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                searchJob?.cancel()
                searchJob = CoroutineScope(Dispatchers.Main).launch {
                    s?.let {
                        if (it.length > 2) { // Set a threshold for triggering the search
                            delay(300) // Debounce time
                            searchViewModel.searchStocks(it.toString())
                        }
                    }
                }
            }
        })
    }

    override fun onItemClick(position: Int) {
        val item = searchViewModel.searchReponse.bestMatches[position]
        if (item.type!="Equity"){
            Snackbar.make(binding.root, "${item.type} is not supported yet by AlphaVantage.", Snackbar.LENGTH_LONG).show()
        }else{


            val bundle = Bundle().apply {
                val gson = Gson()
                val stock = Stock(ticker = item.symbol, price =  "N/A", change_amount = "N/A", change_percentage = "N/A", volume = Constants.Raw.PASSED_FROM_SEARCH)
                val jsonStock = gson.toJson(stock)
                putString(Constants.Keys.BUNDLE_STOCK_DATA, jsonStock)
            }

            val intent = Intent(this, StockDetailActivity::class.java)
            intent.putExtras(bundle)
            startActivity(intent)

        }
    }


}