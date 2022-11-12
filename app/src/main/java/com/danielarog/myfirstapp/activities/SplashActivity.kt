package com.danielarog.myfirstapp.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.danielarog.myfirstapp.databinding.ActivitySplashBinding
import com.google.firebase.auth.FirebaseAuth
import java.util.*

class SplashActivity : AppCompatActivity() {


    lateinit var binding : ActivitySplashBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val cartImage = binding.appLogo

        cartImage.animate()
            .setDuration(2500)
            .rotation(360f)
            .start()

        val timer = Timer()
        timer.schedule(object : TimerTask() {
            override fun run() {
                /*if(FirebaseAuth.getInstance().currentUser !=null) {
                    startActivity(Intent(this@SplashActivity,MainActivity::class.java))
                    return
                }*/
                startActivity(Intent(this@SplashActivity,AuthActivity::class.java))
            }
        },3000)
    }
}