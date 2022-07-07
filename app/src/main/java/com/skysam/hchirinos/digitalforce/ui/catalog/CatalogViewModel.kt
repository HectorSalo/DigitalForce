package com.skysam.hchirinos.digitalforce.ui.catalog

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.skysam.hchirinos.digitalforce.dataClass.Product
import com.skysam.hchirinos.digitalforce.repositories.CatalogRepository

class CatalogViewModel : ViewModel() {
    val products: LiveData<MutableList<Product>> = CatalogRepository.getCatalog().asLiveData()

    fun uploadImage(uri: Uri): LiveData<String> {
        return CatalogRepository.uploadImage(uri).asLiveData()
    }

    fun saveProduct(product: Product) {
        CatalogRepository.saveProduct(product)
    }

    fun deleteProduct(product: Product) {
        CatalogRepository.deleteProduct(product)
    }
}