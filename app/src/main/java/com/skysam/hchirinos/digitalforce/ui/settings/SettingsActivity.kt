package com.skysam.hchirinos.digitalforce.ui.settings

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.skysam.hchirinos.digitalforce.R

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
}