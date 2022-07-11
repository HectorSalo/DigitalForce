package com.skysam.hchirinos.digitalforce.ui.expenses

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.skysam.hchirinos.digitalforce.common.Classes
import com.skysam.hchirinos.digitalforce.dataClass.Expense
import com.skysam.hchirinos.digitalforce.dataClass.Product
import com.skysam.hchirinos.digitalforce.repositories.ExpensesRepository

class ExpensesViewModel : ViewModel() {
    val expenses: LiveData<MutableList<Expense>> = ExpensesRepository.getExpenses().asLiveData()
    val valueWeb: LiveData<String> = Classes.getValueWeb().asLiveData()

    private val _productsInList = MutableLiveData<MutableList<Product>>().apply { value = mutableListOf() }
    val productsInList: LiveData<MutableList<Product>> get() = _productsInList

    private val _priceTotal = MutableLiveData<Double>().apply { value = 0.0 }
    val priceTotal: LiveData<Double> get() = _priceTotal

    fun addExpense(expense: Expense) {
        ExpensesRepository.addExpense(expense)
    }

    fun deleteExpense(expense: Expense) {
        ExpensesRepository.deleteExpense(expense)
    }

    fun clearFields() {
        _productsInList.value?.clear()
        _priceTotal.value = 0.0
    }

    fun addProductInList(product: Product) {
        _productsInList.value?.add(product)
        _productsInList.value = _productsInList.value
        sumPrice(product)
    }

    fun removeProductInList(product: Product) {
        _productsInList.value?.remove(product)
        _productsInList.value = _productsInList.value
        restPrice(product)
    }

    private fun sumPrice(product: Product) {
        val subtotal = product.quantity * product.price
        _priceTotal.value = _priceTotal.value!! + subtotal
    }

    private fun restPrice(product: Product) {
        val subtotal = product.quantity * product.price
        _priceTotal.value = _priceTotal.value!! - subtotal
    }

    fun updateProduct(product: Product, position: Int) {
        val productBefore = _productsInList.value?.get(position)
        val priceBefore = productBefore!!.price * productBefore.quantity
        val priceAfter = product.price * product.quantity
        _productsInList.value?.set(position, product)
        _productsInList.value = _productsInList.value
        _priceTotal.value = _priceTotal.value!! + priceAfter - priceBefore
    }
}