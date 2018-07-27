package com.example.naville.kotlinmap

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.support.v7.app.AppCompatActivity
import android.widget.ProgressBar
import com.airbnb.lottie.LottieAnimationView
import com.example.naville.kotlinmap.util.geo.location.GPS

class HandleActivity : AppCompatActivity() {

    private var lottieAnimationView: LottieAnimationView? = null
    var progressCounter: Int? = 10
    private lateinit var countDownTimer: CountDownTimer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_handler_animation)
        val progressBar: ProgressBar? = findViewById(R.id.progressBar)

        lottieAnimationView = this.findViewById(R.id.lottieAnimID)
        lottieAnimationView!!.playAnimation()

        countDownTimer = object : CountDownTimer(6000, 1000) {
            override fun onFinish() {
                progressBar!!.clearAnimation()
                startActivity(Intent(applicationContext, MainActivity::class.java))
                finish()

            }

            override fun onTick(millisUntilFinished: Long) {

                progressBar!!.progress = progressCounter!!
                progressCounter = progressCounter!! * 2

            }
        }
        countDownTimer.start()

//        Timer().schedule(5000) {
//            startActivity(Intent(applicationContext, MainActivity::class.java))
//            finish()
//        }

    }

}