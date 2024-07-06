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
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.alok.groww.Core.utils.Constants
import com.alok.groww.Core.utils.ExtensionsUtil.gone
import com.alok.groww.Core.utils.ExtensionsUtil.toast
import com.alok.groww.Core.utils.ExtensionsUtil.visible
import com.alok.groww.Detail.presentation.StockDetailActivity
import com.alok.groww.Explore.domain.models.Stock
import com.alok.groww.Explore.presentation.adapters.TrendAdapter
import com.alok.groww.Explore.presentation.viewmodels.TrendPageViewModel
import com.alok.groww.MainActivity
import com.alok.groww.R
import com.alok.groww.databinding.FragmentTrendBinding

class TrendPageFragment(val stocks : List<Stock>, val type: Int): Fragment(), TrendAdapter.OnItemClickListener {


    companion object {
        fun newInstance(stocks: List<Stock>, type: Int) = TrendPageFragment(stocks, type)

    }

    private val viewModel: TrendPageViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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

        adapter = TrendAdapter(stocks.toMutableList(),type,requireContext(), this)
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
            putString(Constants.Keys.BUNDLE_SYMBOL_KEY, stocks[position].ticker)
        }

        val intent = Intent(activity, StockDetailActivity::class.java)
        intent.putExtras(bundle)
        startActivity(intent)

    }


}