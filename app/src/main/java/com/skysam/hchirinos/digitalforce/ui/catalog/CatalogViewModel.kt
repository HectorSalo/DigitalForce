package com.skysam.hchirinos.digitalforce.ui.catalog

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.skysam.hchirinos.digitalforce.dataClass.Product
import com.skysam.hchirinos.digitalforce.repositories.CatalogRepository

class CatalogViewModel : ViewModel() {
    val products: LiveData<MutableList<Product>> = CatalogRepository.getCatalog().asLiveData()

    private val _productToEdit = MutableLiveData<Product>()
    val productToEdit: LiveData<Product> get() = _productToEdit

    fun uploadImage(uri: Uri): LiveData<String> {
        return CatalogRepository.uploadImage(uri).asLiveData()
    }
    fun deleteOldImage(image: String) {
        CatalogRepository.deleteImage(image)
    }

    fun saveProduct(product: Product) {
        CatalogRepository.saveProduct(product)
    }

    fun deleteProduct(product: Product) {
        CatalogRepository.deleteProduct(product)
    }

    fun updateProduct(product: Product) {
        CatalogRepository.updateProduct(product)
    }

    fun productToEdit(product: Product) {
        _productToEdit.value = product
    }
}