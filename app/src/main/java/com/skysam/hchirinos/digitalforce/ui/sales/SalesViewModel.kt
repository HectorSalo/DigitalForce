package com.skysam.hchirinos.digitalforce.ui.sales

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.skysam.hchirinos.digitalforce.dataClass.Customer
import com.skysam.hchirinos.digitalforce.dataClass.Product
import com.skysam.hchirinos.digitalforce.repositories.CustomerRepository

class SalesViewModel : ViewModel() {
    val customers: LiveData<MutableList<Customer>> = CustomerRepository.getCustomers().asLiveData()

    private val _productsInList = MutableLiveData<MutableList<Product>>().apply { value = mutableListOf() }
    val productsInList: LiveData<MutableList<Product>> get() = _productsInList

    fun addProductInList(product: Product) {
        _productsInList.value?.add(product)
        _productsInList.value = _productsInList.value
    }

    fun removeProductInList(product: Product) {
        _productsInList.value?.remove(product)
        _productsInList.value = _productsInList.value
    }
}