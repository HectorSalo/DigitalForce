package com.skysam.hchirinos.digitalforce.repositories

import android.content.ContentValues
import android.util.Log
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.skysam.hchirinos.digitalforce.common.Classes
import com.skysam.hchirinos.digitalforce.common.Constants
import com.skysam.hchirinos.digitalforce.dataClass.Product
import com.skysam.hchirinos.digitalforce.dataClass.Sale
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

/**
 * Created by Hector Chirinos on 04/11/2022.
 */

object SaleRepository {
    private val PATH = when(Classes.getEnviroment()) {
        Constants.DEMO -> Constants.SALES_DEMO
        Constants.RELEASE -> Constants.SALES
        else -> Constants.SALES
    }

    private fun getInstance(): CollectionReference {
        return FirebaseFirestore.getInstance().collection(PATH)
    }

    fun getSales(): Flow<MutableList<Sale>> {
        return callbackFlow {
            val request = getInstance()
                .orderBy(Constants.DATE, Query.Direction.DESCENDING)
                .addSnapshotListener { value, error ->
                    if (error != null) {
                        Log.w(ContentValues.TAG, "Listen failed.", error)
                        return@addSnapshotListener
                    }

                    val sales = mutableListOf<Sale>()
                    for (sale in value!!) {
                        val listProducts = mutableListOf<Product>()
                        if (sale.get(Constants.PRODUCTS) != null) {
                            @Suppress("UNCHECKED_CAST")
                            val products = sale.data.getValue(Constants.PRODUCTS) as ArrayList<HashMap<String, Any>>
                            for (prod in products) {
                                val product = Product(
                                    "",
                                    prod[Constants.NAME].toString(),
                                    prod[Constants.PRICE].toString().toDouble(),
                                    prod[Constants.QUANTITY].toString().toInt(),
                                    "",
                                    null
                                )
                                listProducts.add(product)
                            }
                        }
                        val saleNew = Sale(
                            sale.id,
                            sale.getString(Constants.NAME)!!,
                            sale.getString(Constants.LOCATION)!!,
                            sale.getDouble(Constants.INVOICE)!!.toInt(),
                            listProducts,
                            sale.getDouble(Constants.PRICE)!!,
                            sale.getDate(Constants.DATE)!!
                        )
                        sales.add(saleNew)
                    }
                    trySend(sales)
                }
            awaitClose { request.remove() }
        }
    }

    fun addSale(sale: Sale) {
        val data = hashMapOf(
            Constants.NAME to sale.nameCustomer,
            Constants.LOCATION to sale.location,
            Constants.INVOICE to sale.invoice,
            Constants.PRODUCTS to sale.listProducts,
            Constants.PRICE to sale.total,
            Constants.DATE to sale.date,
            Constants.RATE to sale.rate
        )
        getInstance().add(data)
    }

    fun deleteSale(sale: Sale) {
        getInstance().document(sale.id)
            .delete()
    }
}