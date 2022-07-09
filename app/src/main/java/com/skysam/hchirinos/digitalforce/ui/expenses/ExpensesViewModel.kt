package com.skysam.hchirinos.digitalforce.ui.expenses

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.skysam.hchirinos.digitalforce.dataClass.Expense
import com.skysam.hchirinos.digitalforce.repositories.ExpensesRepository

class ExpensesViewModel : ViewModel() {
    val expenses: LiveData<MutableList<Expense>> = ExpensesRepository.getExpenses().asLiveData()
}