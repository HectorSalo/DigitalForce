package com.skysam.hchirinos.digitalforce.ui.stats

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.skysam.hchirinos.digitalforce.dataClass.Expense
import com.skysam.hchirinos.digitalforce.dataClass.Sale
import com.skysam.hchirinos.digitalforce.repositories.ExpensesRepository
import com.skysam.hchirinos.digitalforce.repositories.SaleRepository

class StatsViewModel : ViewModel() {
    val sales: LiveData<MutableList<Sale>> = SaleRepository.getSales().asLiveData()
    val expenses: LiveData<MutableList<Expense>> = ExpensesRepository.getExpenses().asLiveData()
}