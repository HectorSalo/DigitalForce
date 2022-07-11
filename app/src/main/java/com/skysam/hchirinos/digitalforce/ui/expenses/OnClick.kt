package com.skysam.hchirinos.digitalforce.ui.expenses

import com.skysam.hchirinos.digitalforce.dataClass.Expense

interface OnClick {
    fun viewExpense(expense: Expense)
    fun edit(expense: Expense)
    fun delete(expense: Expense)
}
