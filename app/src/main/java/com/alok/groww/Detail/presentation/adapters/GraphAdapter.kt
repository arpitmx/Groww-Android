package com.alok.groww.Detail.presentation.adapters

import com.robinhood.spark.SparkAdapter

class GraphAdapter(var ydata: MutableList<Float>) : SparkAdapter() {


    fun updateYData(newData: List<Float>) {
        ydata.clear()
        ydata.addAll(newData)
        notifyDataSetChanged()
    }


    override fun getCount(): Int {
       return ydata.size
    }

    override fun getItem(index: Int): Any {
        return ydata[index]
    }

    override fun hasBaseLine(): Boolean {
        return false
    }

    override fun getBaseLine(): Float {
        return super.getBaseLine()
    }

    override fun getY(index: Int): Float {
        return ydata[index];

    }
}