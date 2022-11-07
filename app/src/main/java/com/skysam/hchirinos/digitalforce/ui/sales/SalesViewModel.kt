package com.skysam.hchirinos.digitalforce.ui.sales

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.skysam.hchirinos.digitalforce.dataClass.Customer
import com.skysam.hchirinos.digitalforce.dataClass.Product
import com.skysam.hchirinos.digitalforce.dataClass.Sale
import com.skysam.hchirinos.digitalforce.repositories.CustomerRepository
import com.skysam.hchirinos.digitalforce.repositories.SaleRepository

class SalesViewModel : ViewModel() {
    val sales: LiveData<MutableList<Sale>> = SaleRepository.getSales().asLiveData()
    val customers: LiveData<MutableList<Customer>> = CustomerRepository.getCustomers().asLiveData()

    private val _productsInList = MutableLiveData<MutableList<Product>>().apply { value = mutableListOf() }
    val productsInList: LiveData<MutableList<Product>> get() = _productsInList

    private val _saleToSend = MutableLiveData<Sale>()
    val saleToSend: LiveData<Sale> get() = _saleToSend

    private val _saleToView = MutableLiveData<Sale>()
    val saleToView: LiveData<Sale> get() = _saleToView

    fun addProductInList(product: Product) {
        _productsInList.value?.add(product)
        _productsInList.value = _productsInList.value
    }

    fun removeProductInList(product: Product) {
        _productsInList.value?.remove(product)
        _productsInList.value = _productsInList.value
    }

    fun sendSale(sale: Sale) {
        _saleToSend.value = sale
    }

    fun addSale(sale: Sale) {
        SaleRepository.addSale(sale)
    }

    fun deleteSale(sale: Sale) {
        SaleRepository.deleteSale(sale)
    }

    fun viewSale(sale: Sale) {
        _saleToView.value = sale
    }
}