package com.skysam.hchirinos.digitalforce.ui.catalog

import com.skysam.hchirinos.digitalforce.dataClass.Product

/**
 * Created by Hector Chirinos on 07/07/2022.
 */

interface OnClick {
    fun delete(product: Product)
    fun edit(product: Product)
}