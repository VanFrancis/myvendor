package com.mobilepoc.myvendor.view.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.mobilepoc.myvendor.R
import com.mobilepoc.myvendor.databinding.ActivityProductDetailsBinding

class ProductDetailsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityProductDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}