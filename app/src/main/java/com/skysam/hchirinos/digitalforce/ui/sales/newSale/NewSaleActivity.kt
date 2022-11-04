package com.skysam.hchirinos.digitalforce.ui.sales.newSale

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.skysam.hchirinos.digitalforce.databinding.ActivityNewSaleBinding

class NewSaleActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNewSaleBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        binding = ActivityNewSaleBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}