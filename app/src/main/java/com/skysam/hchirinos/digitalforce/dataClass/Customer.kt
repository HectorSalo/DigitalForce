package com.skysam.hchirinos.digitalforce.dataClass

/**
 * Created by Hector Chirinos on 30/05/2022.
 */

data class Customer(
 var id: String,
 var name: String,
 var typeIdentifier: String,
 var numberIdentifier: Int,
 var locations: MutableList<String>
)
