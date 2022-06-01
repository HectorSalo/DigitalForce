package com.skysam.hchirinos.digitalforce.common

import android.app.Application
import android.content.Context

/**
 * Created by Hector Chirinos on 31/05/2022.
 */

class DigitalForce: Application() {
    companion object {
        lateinit var appContext: Context
    }

    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext
    }

    object DigitalForce {
        fun getContext(): Context {
            return appContext
        }
    }
}