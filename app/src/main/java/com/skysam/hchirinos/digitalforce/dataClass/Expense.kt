package com.skysam.hchirinos.digitalforce.dataClass

import java.util.*

/**
 * Created by Hector Chirinos on 09/07/2022.
 */

data class Expense(
 val id: String,
 var listProducts: MutableList<Product>,
 var total: Double,
 var date: Date,
 var rate: Double
)
