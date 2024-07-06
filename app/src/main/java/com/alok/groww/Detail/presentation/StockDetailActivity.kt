package com.alok.groww.Detail.presentation

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.alok.groww.Core.utils.Constants
import com.alok.groww.R
import com.alok.groww.databinding.ActivityStockDetailBinding

class StockDetailActivity : AppCompatActivity() {

    private val binding : ActivityStockDetailBinding by lazy {
        ActivityStockDetailBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setViews()


    }


    private fun setViews(){
        val bundle: Bundle? = intent.extras
        if (bundle != null) {
            val tickerSymbol = bundle.getString(Constants.Keys.BUNDLE_SYMBOL_KEY)
        }
    }
}