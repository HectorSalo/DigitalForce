package com.skysam.hchirinos.digitalforce.ui.services

import com.skysam.hchirinos.digitalforce.dataClass.Service

/**
 * Created by Hector Chirinos on 10/01/2023.
 */

interface OnClick {
    fun view(service: Service)
    fun delete(service: Service)
}