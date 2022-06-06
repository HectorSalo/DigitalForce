package com.skysam.hchirinos.digitalforce.ui.catalog

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.skysam.hchirinos.digitalforce.dataClass.Product
import com.skysam.hchirinos.digitalforce.repositories.CatalogRepository

class CatalogViewModel : ViewModel() {
    val products: LiveData<MutableList<Product>> = CatalogRepository.getCatalog().asLiveData()
}