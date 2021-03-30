package com.mobilepoc.myvendor.view.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.mobilepoc.myvendor.R
import com.mobilepoc.myvendor.databinding.ActivityAddProductBinding

class AddProductActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityAddProductBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}