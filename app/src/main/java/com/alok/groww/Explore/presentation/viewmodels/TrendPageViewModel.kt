package com.alok.groww.Explore.presentation.viewmodels

import androidx.lifecycle.ViewModel
import com.alok.groww.Explore.domain.models.Stock

class TrendPageViewModel : ViewModel() {
    lateinit var list: List<Stock>
}