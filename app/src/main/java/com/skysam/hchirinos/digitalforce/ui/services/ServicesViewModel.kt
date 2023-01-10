package com.skysam.hchirinos.digitalforce.ui.services

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.skysam.hchirinos.digitalforce.dataClass.Customer
import com.skysam.hchirinos.digitalforce.dataClass.Product
import com.skysam.hchirinos.digitalforce.dataClass.Sale
import com.skysam.hchirinos.digitalforce.dataClass.Service
import com.skysam.hchirinos.digitalforce.repositories.CustomerRepository
import com.skysam.hchirinos.digitalforce.repositories.SaleRepository
import com.skysam.hchirinos.digitalforce.repositories.ServiceRepository

class ServicesViewModel : ViewModel() {
    val services: LiveData<MutableList<Service>> = ServiceRepository.getServices().asLiveData()
    val customers: LiveData<MutableList<Customer>> = CustomerRepository.getCustomers().asLiveData()

    private val _productsInList = MutableLiveData<MutableList<Product>>().apply { value = mutableListOf() }
    val productsInList: LiveData<MutableList<Product>> get() = _productsInList

    private val _serviceToSend = MutableLiveData<Service>()
    val serviceToSend: LiveData<Service> get() = _serviceToSend

    private val _serviceToView = MutableLiveData<Service>()
    val serviceToView: LiveData<Service> get() = _serviceToView

    fun addProductInList(product: Product) {
        _productsInList.value?.add(product)
        _productsInList.value = _productsInList.value
    }

    fun removeProductInList(product: Product) {
        _productsInList.value?.remove(product)
        _productsInList.value = _productsInList.value
    }

    fun sendService(service: Service) {
        _serviceToSend.value = service
    }

    fun addService(service: Service) {
        ServiceRepository.addService(service)
    }

    fun deleteService(service: Service) {
        ServiceRepository.deleteService(service)
    }

    fun viewService(service: Service) {
        _serviceToView.value = service
    }
}