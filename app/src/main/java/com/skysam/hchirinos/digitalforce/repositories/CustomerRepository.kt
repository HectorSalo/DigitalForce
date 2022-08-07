package com.skysam.hchirinos.digitalforce.repositories

import android.content.ContentValues
import android.util.Log
import com.google.firebase.firestore.*
import com.skysam.hchirinos.digitalforce.common.Classes
import com.skysam.hchirinos.digitalforce.common.Constants
import com.skysam.hchirinos.digitalforce.dataClass.Customer
import com.skysam.hchirinos.digitalforce.dataClass.Product
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

/**
 * Created by Hector Chirinos on 30/05/2022.
 */

object CustomerRepository {
    private val PATH = when(Classes.getEnviroment()) {
        Constants.DEMO -> Constants.CUSTOMERS_DEMO
        Constants.RELEASE -> Constants.CUSTOMERS
        else -> Constants.CUSTOMERS
    }

    private fun getInstance(): CollectionReference {
        return FirebaseFirestore.getInstance().collection(PATH)
    }

    fun addCostumer(customer: Customer) {
        val data = hashMapOf(
            Constants.NAME to customer.name,
            Constants.TYPE_IDENTIFIER to customer.typeIdentifier,
            Constants.NUMBER_IDENTIFIER to customer.numberIdentifier,
            Constants.LOCATIONS to customer.locations
        )
        getInstance().add(data)
    }

    fun getCustomers(): Flow<MutableList<Customer>> {
        return callbackFlow {
            val request = getInstance()
                .orderBy(Constants.NAME, Query.Direction.ASCENDING)
                .addSnapshotListener(MetadataChanges.INCLUDE) { value, error ->
                    if (error != null) {
                        Log.w(ContentValues.TAG, "Listen failed.", error)
                        return@addSnapshotListener
                    }

                    val customers: MutableList<Customer> = mutableListOf()
                    for (doc in value!!) {
                        var listLocation = mutableListOf<String>()
                        if (doc.get(Constants.LOCATIONS) != null) {
                            @Suppress("UNCHECKED_CAST")
                            listLocation = doc.get(Constants.LOCATIONS) as MutableList<String>
                        }
                        var listProducts = mutableListOf<Product>()
                        if (doc.get(Constants.LOCATIONS) != null) {
                            @Suppress("UNCHECKED_CAST")
                            listProducts = doc.get(Constants.PRODUCTS) as MutableList<Product>
                        }
                        val customer = Customer(
                            doc.id,
                            doc.getString(Constants.NAME)!!,
                            doc.getString(Constants.TYPE_IDENTIFIER)!!,
                            doc.getDouble(Constants.NUMBER_IDENTIFIER)!!.toInt(),
                            listLocation,
                            listProducts
                        )
                        customers.add(customer)
                    }
                    trySend(customers)
                }
            awaitClose { request.remove() }
        }
    }

    fun updateCustomer(customer: Customer) {
        val data: Map<String, Any> = hashMapOf(
            Constants.NAME to customer.name,
            Constants.TYPE_IDENTIFIER to customer.typeIdentifier,
            Constants.NUMBER_IDENTIFIER to customer.numberIdentifier,
            Constants.LOCATIONS to customer.locations
        )
        getInstance().document(customer.id)
            .update(data)
    }

    fun deleteLocations(id: String, locations: MutableList<String>) {
        for (loc in locations) {
            getInstance().document(id)
                .update(Constants.LOCATIONS, FieldValue.arrayRemove(loc))
        }
    }

    fun deleteCustomer(customer: Customer) {
        getInstance().document(customer.id)
            .delete()
    }
}