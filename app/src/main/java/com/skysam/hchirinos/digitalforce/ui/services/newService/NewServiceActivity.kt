package com.skysam.hchirinos.digitalforce.ui.services.newService

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.skysam.hchirinos.digitalforce.databinding.ActivityNewServiceBinding

class NewServiceActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNewServiceBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        binding = ActivityNewServiceBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

}