package com.impApp.a7minutesworkout

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.impApp.a7minutesworkout.databinding.ActivityFinishBinding

class FinishActivity : AppCompatActivity() {

    private var binding: ActivityFinishBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFinishBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setSupportActionBar(binding?.toolbarFinishActivity)
        if(supportActionBar != null){
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
        binding?.toolbarFinishActivity?.setNavigationOnClickListener{
            onBackPressed()
        }
        binding?.btnFinish?.setOnClickListener{
            finish()
        }

    }
}