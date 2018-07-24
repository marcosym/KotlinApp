package com.example.naville.kotlinmap

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Window
import com.airbnb.lottie.LottieAnimationView
import java.util.*
import kotlin.concurrent.schedule

class HandleActivity : AppCompatActivity() {

    private var lottieAnimationView: LottieAnimationView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_handler_animation)

        lottieAnimationView = findViewById(R.id.lottieAnimID)
        lottieAnimationView!!.playAnimation()

        Timer().schedule(5000){
            startActivity(Intent(applicationContext, HandleActivity::class.java))
            finish()
        }

    }
}