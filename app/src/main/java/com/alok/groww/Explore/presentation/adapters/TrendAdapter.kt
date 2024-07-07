package com.alok.groww.Explore.presentation.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.alok.groww.Core.utils.Constants
import com.alok.groww.Core.utils.ExtensionsUtil.gone
import com.alok.groww.Core.utils.ExtensionsUtil.visible
import com.alok.groww.Explore.domain.models.Stock
import com.alok.groww.R
import com.alok.groww.databinding.StockItemBinding
import timber.log.Timber
import java.util.Locale
import java.util.Random

class TrendAdapter(val stockList: MutableList<Stock>, val trendType: Int, val context: Context, val listener : OnItemClickListener) : RecyclerView.Adapter<TrendAdapter.StockViewHolder>() {

    companion object{
        const val GAINER = 1
        const val LOSER = 2
        const val MOST_TRADED = 3
    }


   override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StockViewHolder {
        val binding = StockItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)


       return StockViewHolder(binding).apply {

           itemView.setOnClickListener{
               if (bindingAdapterPosition != RecyclerView.NO_POSITION) {
                   listener.onItemClick(position = bindingAdapterPosition)
               }
           }

       }
    }

    override fun onBindViewHolder(holder: StockViewHolder, position: Int) {
        holder.bind(stockList[position])
    }

    override fun getItemCount(): Int = stockList.size

    fun setStocks(stocks: List<Stock>) {
        stockList.clear()
        stockList.addAll(stocks)
        notifyDataSetChanged()
    }

    private fun setGrowthUi(binding : StockItemBinding, trendTypex: Int){
        if (trendTypex == GAINER){
            binding.trendIcon.rotation =0f
            binding.trendIcon.setColorFilter(ContextCompat.getColor(context, R.color.colorPrimaryDark))
            binding.tvPriceChange.setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryDark))
        }else if (trendTypex == LOSER){
            binding.trendIcon.rotation = 180f
            binding.trendIcon.setColorFilter(ContextCompat.getColor(context, R.color.mainTertiaryLoss))
            binding.tvPriceChange.setTextColor(ContextCompat.getColor(context, R.color.mainTertiaryLoss))
        }
    }

    private fun getTrendType(changePercentage: String): Int {
        val percentageValue = changePercentage.replace("%", "").toFloat()
        return if (percentageValue > 0) Constants.Raw.GAINER else Constants.Raw.LOSER
    }

    private fun aggregateVolume(volume : String):String{
        val volume = "Vol ${volume.toInt()/1000}k"
        return volume
    }

    inner class StockViewHolder(private val binding: StockItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(stock: Stock) {

            Timber.d("Stock123: ${stock.ticker}")
            binding.tvStockPrice.text = "$${stock.price}"
            binding.tvCompanyName.text = stock.ticker
            binding.tvPriceChange.text = "${stock.change_percentage}%"
            if(trendType == Constants.Raw.MOST_TRADED){
                binding.tvStockVolume.visible()
                binding.tvStockVolume.text = aggregateVolume(stock.volume)
            }else{
                binding.tvStockVolume.gone()
            }

            setupCircularSymbolView(binding.parentIcon, stock.ticker, binding.tvSymbol)
            setGrowthUi(binding,getTrendType(stock.change_percentage))
        }
    }


    fun setupCircularSymbolView(view: FrameLayout, symbol: String, tv : TextView) {

        // Set the text to the first two letters of the symbol
        tv.text = symbol.take(2).uppercase(Locale.getDefault())

        // Generate a random color
        val randomColor = generateRandomColor()
        view.background.setTint(randomColor)
    }

    private fun generateRandomColor(): Int {
        val rnd = Random()
        return Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }



}
