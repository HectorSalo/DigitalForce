package com.skysam.hchirinos.digitalforce.ui.customer

import com.skysam.hchirinos.digitalforce.dataClass.Customer

/**
 * Created by Hector Chirinos on 01/06/2022.
 */

interface OnClick {
    fun deleteLocation(customer: Customer)
    fun delete(customer: Customer)
    fun edit(customer: Customer)
}