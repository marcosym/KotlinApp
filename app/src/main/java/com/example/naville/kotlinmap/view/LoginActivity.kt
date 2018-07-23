package com.example.naville.kotlinmap.view

import android.os.Bundle
import android.support.design.widget.TextInputEditText
import android.support.v7.app.AppCompatActivity
import android.widget.TextView
import com.example.naville.kotlinmap.R
import com.example.naville.kotlinmap.util.Fonts

class LoginActivity : AppCompatActivity(){

    var signInLabel : TextView? = null
    var emailLabel : TextInputEditText? = null
    var passwordLabel : TextInputEditText? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        findViews()
        applyCustomFont()

    }

    private fun applyCustomFont() {
        Fonts.nunitoFontBold(this, signInLabel)
        Fonts.nunitoFontRegular(this, emailLabel)
        Fonts.nunitoFontRegular(this, passwordLabel)    }

    private fun findViews() {
        signInLabel = findViewById(R.id.signInLabel)
        emailLabel = findViewById(R.id.emailLabel)
        passwordLabel = findViewById(R.id.passwordLabel)
    }
}