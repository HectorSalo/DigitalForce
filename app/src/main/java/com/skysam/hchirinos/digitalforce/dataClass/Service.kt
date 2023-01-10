package com.skysam.hchirinos.digitalforce.dataClass

import java.util.*

/**
 * Created by Hector Chirinos on 09/01/2023.
 */

data class Service(
 val id: String,
 val nameCustomer: String,
 val location: String,
 var invoice: Int,
 var list: MutableList<Product>,
 var total: Double,
 var date: Date,
 var rate: Double = 0.0
)
