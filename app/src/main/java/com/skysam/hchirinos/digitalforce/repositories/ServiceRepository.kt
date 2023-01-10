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
import com.skysam.hchirinos.digitalforce.dataClass.Service
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

/**
 * Created by Hector Chirinos on 09/01/2023.
 */

object ServiceRepository {
    private val PATH = when(Classes.getEnviroment()) {
        Constants.DEMO -> Constants.SERVICES_DEMO
        Constants.RELEASE -> Constants.SERVICES
        else -> Constants.SERVICES
    }

    private fun getInstance(): CollectionReference {
        return FirebaseFirestore.getInstance().collection(PATH)
    }

    fun getServices(): Flow<MutableList<Service>> {
        return callbackFlow {
            val request = getInstance()
                .orderBy(Constants.DATE, Query.Direction.DESCENDING)
                .addSnapshotListener { value, error ->
                    if (error != null) {
                        Log.w(ContentValues.TAG, "Listen failed.", error)
                        return@addSnapshotListener
                    }

                    val services = mutableListOf<Service>()
                    for (service in value!!) {
                        val list = mutableListOf<Product>()
                        if (service.get(Constants.PRODUCTS) != null) {
                            @Suppress("UNCHECKED_CAST")
                            val products = service.data.getValue(Constants.PRODUCTS) as ArrayList<HashMap<String, Any>>
                            for (prod in products) {
                                val product = Product(
                                    "",
                                    prod[Constants.NAME].toString(),
                                    prod[Constants.PRICE].toString().toDouble(),
                                    prod[Constants.QUANTITY].toString().toInt(),
                                    "",
                                    null
                                )
                                list.add(product)
                            }
                        }
                        val serviceNew = Service(
                            service.id,
                            service.getString(Constants.NAME)!!,
                            service.getString(Constants.LOCATION)!!,
                            service.getDouble(Constants.INVOICE)!!.toInt(),
                            list,
                            service.getDouble(Constants.PRICE)!!,
                            service.getDate(Constants.DATE)!!
                        )
                        services.add(serviceNew)
                    }
                    trySend(services)
                }
            awaitClose { request.remove() }
        }
    }

    fun addService(service: Service) {
        val data = hashMapOf(
            Constants.NAME to service.nameCustomer,
            Constants.LOCATION to service.location,
            Constants.INVOICE to service.invoice,
            Constants.PRODUCTS to service.list,
            Constants.PRICE to service.total,
            Constants.DATE to service.date,
            Constants.RATE to service.rate
        )
        getInstance().add(data)
    }

    fun deleteService(service: Service) {
        getInstance().document(service.id)
            .delete()
    }
}