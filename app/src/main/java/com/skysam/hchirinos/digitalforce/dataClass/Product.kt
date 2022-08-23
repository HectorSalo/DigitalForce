package com.skysam.hchirinos.digitalforce.dataClass

/**
 * Created by Hector Chirinos on 05/06/2022.
 */

data class Product(
    val id: String,
    var name: String,
    var price: Double = 1.0,
    var quantity: Int = 1,
    var image: String,
    var pdf: String = ""
)
