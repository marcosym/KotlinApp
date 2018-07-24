package com.example.naville.kotlinmap.view

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.TextInputEditText
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.TextView
import com.example.naville.kotlinmap.HandleActivity
import com.example.naville.kotlinmap.MainActivity
import com.example.naville.kotlinmap.R
import com.example.naville.kotlinmap.util.Fonts
import com.example.naville.kotlinmap.util.GPS

class LoginActivity : AppCompatActivity() {

    var signInLabel: TextView? = null
    var emailLabel: TextInputEditText? = null
    var passwordLabel: TextInputEditText? = null
    var btnProceed: Button? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        findViews()
        applyCustomFont()

//        LoginPres

        btnProceed!!.setOnClickListener {
//            startActivity(Intent(this, MainActivity::class.java))
            startActivity(Intent(this, HandleActivity::class.java))
            finish()
        }

    }

    /*
     * Apply custom fonts
     */
    private fun applyCustomFont() {
        Fonts.nunitoFontBold(this, signInLabel)
        Fonts.nunitoFontRegular(this, emailLabel)
        Fonts.nunitoFontRegular(this, passwordLabel)
    }

    /*
     * Find views
     */
    private fun findViews() {
        signInLabel = findViewById(R.id.signInLabel)
        emailLabel = findViewById(R.id.emailLabel)
        passwordLabel = findViewById(R.id.passwordLabel)
        btnProceed = findViewById(R.id.btnProceed)
    }

    override fun onStart() {
        super.onStart()

        /*
* Get permissions for location
*/
//        Permissions.verifyGPS(this)

        /*
Class GPS initialized
 */
        GPS.liveLocation(this)


    }

}

