package com.skysam.hchirinos.digitalforce.common

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.skysam.hchirinos.digitalforce.BuildConfig

/**
 * Created by Hector Chirinos on 30/05/2022.
 */

object Classes {
    fun getEnviroment(): String {
        return BuildConfig.BUILD_TYPE
    }

    fun close(view: View) {
        val imn = DigitalForce.DigitalForce.getContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imn.hideSoftInputFromWindow(view.windowToken, 0)
    }
}