package com.mobilepoc.myvendor.view.activities

import android.media.audiofx.BassBoost
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.mobilepoc.myvendor.R
import com.mobilepoc.myvendor.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}