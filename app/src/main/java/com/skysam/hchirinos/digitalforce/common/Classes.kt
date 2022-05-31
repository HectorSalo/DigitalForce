package com.skysam.hchirinos.digitalforce.common

import com.skysam.hchirinos.digitalforce.BuildConfig

/**
 * Created by Hector Chirinos on 30/05/2022.
 */

object Classes {
    fun getEnviroment(): String {
        return BuildConfig.BUILD_TYPE
    }
}