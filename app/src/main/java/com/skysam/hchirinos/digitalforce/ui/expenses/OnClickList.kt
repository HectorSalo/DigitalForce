package com.skysam.hchirinos.digitalforce.ui.expenses

import com.skysam.hchirinos.digitalforce.dataClass.Product

/**
 * Created by Hector Chirinos on 10/07/2022.
 */

interface OnClickList {
    fun deleteItem(product: Product)
    fun editItem(product: Product)
}