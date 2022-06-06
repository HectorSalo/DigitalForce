package com.skysam.hchirinos.digitalforce.repositories

import android.content.ContentValues
import android.util.Log
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.MetadataChanges
import com.google.firebase.firestore.Query
import com.skysam.hchirinos.digitalforce.common.Classes
import com.skysam.hchirinos.digitalforce.common.Constants
import com.skysam.hchirinos.digitalforce.dataClass.Product
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

/**
 * Created by Hector Chirinos on 05/06/2022.
 */

object CatalogRepository {
 private val PATH = when(Classes.getEnviroment()) {
  Constants.DEMO -> Constants.CATALOG_DEMO
  Constants.RELEASE -> Constants.CATALOG
  else -> Constants.CATALOG
 }

 private fun getInstance(): CollectionReference {
  return FirebaseFirestore.getInstance().collection(PATH)
 }

 fun getCatalog(): Flow<MutableList<Product>> {
  return callbackFlow {
   val request = getInstance()
    .orderBy(Constants.NAME, Query.Direction.ASCENDING)
    .addSnapshotListener(MetadataChanges.INCLUDE) { value, error ->
     if (error != null) {
      Log.w(ContentValues.TAG, "Listen failed.", error)
      return@addSnapshotListener
     }

     val products: MutableList<Product> = mutableListOf()
     for (doc in value!!) {
      val product = Product(
       doc.id,
       doc.getString(Constants.NAME)!!,
       doc.getDouble(Constants.PRICE)!!,
       image = doc.getString(Constants.IMAGE)!!
      )
      products.add(product)
     }
     trySend(products)
    }
   awaitClose { request.remove() }
  }
 }
}