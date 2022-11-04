package com.skysam.hchirinos.digitalforce.dataClass

import java.util.*

/**
 * Created by Hector Chirinos on 04/11/2022.
 */

data class Sale(
    val id: String,
    val nameCustomer: String,
    val location: String,
    var invoice: Int,
    var listProducts: MutableList<Product>,
    var total: Double,
    var date: Date,
    var rate: Double = 0.0
)
