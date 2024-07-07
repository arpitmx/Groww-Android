package com.alok.groww.Explore.presentation

import android.content.Intent
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.alok.groww.Core.utils.Constants
import com.alok.groww.Core.utils.ExtensionsUtil.gone
import com.alok.groww.Core.utils.ExtensionsUtil.visible
import com.alok.groww.Detail.presentation.StockDetailActivity
import com.alok.groww.Explore.domain.models.Stock
import com.alok.groww.Explore.presentation.adapters.TrendAdapter
import com.alok.groww.Explore.presentation.viewmodels.TrendPageViewModel
import com.alok.groww.databinding.FragmentTrendBinding
import com.google.gson.Gson
import kotlinx.serialization.json.Json
import java.util.ArrayList

class TrendPageFragment(): Fragment(), TrendAdapter.OnItemClickListener {

    private var stocks: List<Stock>? = null
    private var type: Int = 0

    companion object {

        private const val ARG_STOCKS = "stocks"
        private const val ARG_TYPE = "type"

        fun newInstance(stocks: List<Stock>, type: Int): TrendPageFragment {
            val fragment = TrendPageFragment()
            val args = Bundle()
            args.putParcelableArrayList(ARG_STOCKS, ArrayList(stocks))  // Assuming Stock implements Parcelable
            args.putInt(ARG_TYPE, type)
            fragment.arguments = args
            return fragment
        }

    }

    private val viewModel: TrendPageViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            stocks = it.getParcelableArrayList(ARG_STOCKS)
            type = it.getInt(ARG_TYPE)
        }

        viewModel.list = stocks!!
    }

    private lateinit var binding : FragmentTrendBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTrendBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Handler(Looper.getMainLooper()).postDelayed({setupViews()},500)

    }

    lateinit var adapter: TrendAdapter
    private fun setupViews() {

        adapter = TrendAdapter(viewModel.list.toMutableList(),type,requireContext(), this)
        binding.trendRV.layoutManager = GridLayoutManager(context,2)
        binding.trendRV.setHasFixedSize(true)
        binding.trendRV.adapter = adapter

    }


    fun setLoadingState(show: Boolean){


            when(show){
                true -> {
                    binding.trendRV.gone()
                    binding.progressParent.visible()
                }
                false -> {
                    binding.progressParent.gone()
                    binding.trendRV.visible()
                }
            }


    }

    override fun onItemClick(position: Int) {
        val bundle = Bundle().apply {
            val gson = Gson()
            val jsonStock = gson.toJson(stocks!![position])
            putString(Constants.Keys.BUNDLE_STOCK_DATA, jsonStock)
        }

        val intent = Intent(activity, StockDetailActivity::class.java)
        intent.putExtras(bundle)
        startActivity(intent)

    }


}