package com.danielarog.myfirstapp.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.danielarog.myfirstapp.databinding.ActivityAuthBinding

class AuthActivity : AppCompatActivity() {

    lateinit var binding : ActivityAuthBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}