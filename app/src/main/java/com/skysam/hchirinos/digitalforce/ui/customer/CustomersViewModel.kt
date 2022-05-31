package com.skysam.hchirinos.digitalforce.ui.customer

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.skysam.hchirinos.digitalforce.dataClass.Customer
import com.skysam.hchirinos.digitalforce.repositories.CustomerRepository

class CustomersViewModel : ViewModel() {
    val customers: LiveData<MutableList<Customer>> = CustomerRepository.getCustomers().asLiveData()
}