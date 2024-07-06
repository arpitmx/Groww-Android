package com.alok.groww.Explore.presentation.adapters

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.alok.groww.Core.utils.Constants
import com.alok.groww.Explore.domain.models.Stock
import com.alok.groww.Explore.presentation.TrendPageFragment


class ViewPagerAdapter(activity: AppCompatActivity, val gainerList : List<Stock>, val loserList: List<Stock>, val most_traded : List<Stock> ) : FragmentStateAdapter(activity) {

    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> TrendPageFragment.newInstance(gainerList, Constants.Raw.GAINER)
            1 -> TrendPageFragment.newInstance(loserList, Constants.Raw.LOSER)
            2 -> TrendPageFragment.newInstance(most_traded, Constants.Raw.MOST_TRADED)
            else -> throw IllegalStateException("Unexpected position: $position")
        }
    }

}