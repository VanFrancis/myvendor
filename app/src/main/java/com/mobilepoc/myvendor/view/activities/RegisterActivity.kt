package com.mobilepoc.myvendor.view.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.mobilepoc.myvendor.R
import com.mobilepoc.myvendor.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}