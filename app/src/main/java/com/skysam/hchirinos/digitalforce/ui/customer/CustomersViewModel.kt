package com.skysam.hchirinos.digitalforce.ui.customer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.skysam.hchirinos.digitalforce.dataClass.Customer
import com.skysam.hchirinos.digitalforce.repositories.CustomerRepository

class CustomersViewModel : ViewModel() {
    val customers: LiveData<MutableList<Customer>> = CustomerRepository.getCustomers().asLiveData()

    private val _customerToUpdate = MutableLiveData<Customer>()
    val customerToUpdate: LiveData<Customer> get() = _customerToUpdate

    fun customerToUpdate(customer: Customer) {
        _customerToUpdate.value = customer
    }

    fun addCustomer(customer: Customer) {
        CustomerRepository.addCostumer(customer)
    }

    fun updateCustomer(customer: Customer) {
        CustomerRepository.updateCustomer(customer)
    }

    fun deleteLocations(id: String, locations: MutableList<String>) {
        CustomerRepository.deleteLocations(id, locations)
    }

    fun deleteCustomer(customer: Customer) {
        CustomerRepository.deleteCustomer(customer)
    }
}