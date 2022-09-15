package com.skysam.hchirinos.digitalforce.repositories

import android.content.ContentValues
import android.util.Log
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.skysam.hchirinos.digitalforce.common.Classes
import com.skysam.hchirinos.digitalforce.common.Constants
import com.skysam.hchirinos.digitalforce.dataClass.Expense
import com.skysam.hchirinos.digitalforce.dataClass.Product
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

/**
 * Created by Hector Chirinos on 09/07/2022.
 */

object ExpensesRepository {
    private val PATH = when(Classes.getEnviroment()) {
        Constants.DEMO -> Constants.EXPENSES_DEMO
        Constants.RELEASE -> Constants.EXPENSES
        else -> Constants.EXPENSES
    }

    private fun getInstance(): CollectionReference {
        return FirebaseFirestore.getInstance().collection(PATH)
    }

    fun getExpenses(): Flow<MutableList<Expense>> {
        return callbackFlow {
            val request = getInstance()
                .orderBy(Constants.DATE, Query.Direction.DESCENDING)
                .addSnapshotListener { value, error ->
                    if (error != null) {
                        Log.w(ContentValues.TAG, "Listen failed.", error)
                        return@addSnapshotListener
                    }

                    val expenses = mutableListOf<Expense>()
                    for (expense in value!!) {
                        val listProducts = mutableListOf<Product>()
                        if (expense.get(Constants.PRODUCTS) != null) {
                            @Suppress("UNCHECKED_CAST")
                            val products = expense.data.getValue(Constants.PRODUCTS) as ArrayList<HashMap<String, Any>>
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
                        val expenseNew = Expense(
                            expense.id,
                            listProducts,
                            expense.getDouble(Constants.PRICE)!!,
                            expense.getDate(Constants.DATE)!!,
                            expense.getDouble(Constants.RATE)!!
                        )
                        expenses.add(expenseNew)
                    }
                    trySend(expenses)
                }
            awaitClose { request.remove() }
        }
    }

    fun addExpense(expense: Expense) {
        val data = hashMapOf(
            Constants.PRODUCTS to expense.listProducts,
            Constants.PRICE to expense.total,
            Constants.DATE to expense.date,
            Constants.RATE to expense.rate
        )
        getInstance().add(data)
    }

    fun deleteExpense(expense: Expense) {
        getInstance().document(expense.id)
            .delete()
    }
}