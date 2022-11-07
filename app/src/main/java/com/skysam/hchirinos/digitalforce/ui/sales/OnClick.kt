package com.skysam.hchirinos.digitalforce.ui.sales

import com.skysam.hchirinos.digitalforce.dataClass.Sale

/**
 * Created by Hector Chirinos on 06/11/2022.
 */

interface OnClick {
    fun view(sale: Sale)
    fun edit(sale: Sale)
    fun delete(sale: Sale)
}