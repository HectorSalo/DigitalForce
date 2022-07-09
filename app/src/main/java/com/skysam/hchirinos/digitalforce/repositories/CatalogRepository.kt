package com.skysam.hchirinos.digitalforce.repositories

import android.content.ContentValues
import android.net.Uri
import android.util.Log
import com.google.firebase.firestore.*
import com.skysam.hchirinos.digitalforce.R
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.skysam.hchirinos.digitalforce.common.Classes
import com.skysam.hchirinos.digitalforce.common.Constants
import com.skysam.hchirinos.digitalforce.common.DigitalForce
import com.skysam.hchirinos.digitalforce.dataClass.Product
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.util.*

/**
 * Created by Hector Chirinos on 05/06/2022.
 */


object CatalogRepository {
 private val PATH = when(Classes.getEnviroment()) {
  Constants.DEMO -> Constants.CATALOG_DEMO
  Constants.RELEASE -> Constants.CATALOG
  else -> Constants.CATALOG
 }

 private val PATH_STORAGE = when(Classes.getEnviroment()) {
  Constants.DEMO -> "${Constants.CATALOG_DEMO}/"
  Constants.RELEASE -> "${Constants.CATALOG}/"
  else -> "${Constants.CATALOG}/"
 }

 private fun getInstance(): CollectionReference {
  return FirebaseFirestore.getInstance().collection(PATH)
 }

 private fun getInstanceStorage(): StorageReference {
  return Firebase.storage.reference.child(PATH_STORAGE)
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

 fun saveProduct(product: Product) {
  val data = hashMapOf(
   Constants.NAME to product.name.replaceFirstChar {
    if (it.isLowerCase()) it.titlecase(
     Locale.getDefault()
    ) else it.toString()
   },
   Constants.PRICE to product.price,
   Constants.QUANTITY to product.quantity,
   Constants.IMAGE to product.image
  )
  getInstance().add(data)
 }

 fun uploadImage(uri: Uri): Flow<String> {
  return callbackFlow {
   getInstanceStorage()
    .child(uri.lastPathSegment!!)
    .putFile(uri)
    .addOnCompleteListener { task ->
     if (task.isSuccessful) {
      getInstanceStorage().child(uri.lastPathSegment!!)
       .downloadUrl.addOnSuccessListener {
        trySend(it.toString())
       }
     } else {
      trySend(DigitalForce.DigitalForce.getContext().getString(R.string.error_data))
     }
    }
    .addOnProgressListener {
     val progress = (100.0 * it.bytesTransferred) / it.totalByteCount
     trySend(Classes.convertDoubleToString(progress))
    }
   awaitClose {  }
  }
 }

 fun deleteImage(image: String) {
  val ref: StorageReference = Firebase.storage.getReferenceFromUrl(image)
  ref.delete()
 }

 fun updateProduct(product: Product) {
  val data: Map<String, Any> = hashMapOf(
   Constants.NAME to product.name.replaceFirstChar {
    if (it.isLowerCase()) it.titlecase(
     Locale.getDefault()
    ) else it.toString()
   },
   Constants.PRICE to product.price,
   Constants.IMAGE to product.image
  )
  getInstance()
   .document(product.id)
   .update(data)
 }

 fun deleteProduct(product: Product) {
  getInstance().document(product.id)
   .delete()
   .addOnSuccessListener {
    if (product.image.isNotEmpty()) deleteImage(product.image)
   }
 }
}