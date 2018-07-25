package com.example.naville.kotlinmap.view

import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.os.Bundle
import android.support.design.widget.TextInputEditText
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.TextView
import com.example.naville.kotlinmap.HandleActivity
import com.example.naville.kotlinmap.R
import com.example.naville.kotlinmap.util.Fonts
import com.example.naville.kotlinmap.util.GPS
import com.example.naville.kotlinmap.util.Permissions
import com.example.naville.kotlinmap.util.Permissions.Companion.activated
import com.example.naville.kotlinmap.util.Permissions.Companion.proceed
import timber.log.Timber

class LoginActivity : AppCompatActivity() {

    var signInLabel: TextView? = null
    var emailLabel: TextInputEditText? = null
    var passwordLabel: TextInputEditText? = null
    var btnProceed: Button? = null
    private var locationManager: LocationManager? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        locationManager = getSystemService(Context.LOCATION_SERVICE) as? LocationManager

        findViews()
        applyCustomFont()
        initActions()

    }

    private fun initActions() {

        /*
         * Button proceed action listener
         */
        btnProceed!!.setOnClickListener {
            if (!proceed) {
                verifyGPSConditions()
            } else {
                startActivity(Intent(this, HandleActivity::class.java))
                finish()
            }
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

    /*
     * Verify GPS conditions before start another activity
     */
    private fun verifyGPSConditions() {

        /*
* Get permissions for location
*/
        if (!activated) {
            assert(locationManager != null)
            Permissions.verifyGPS(this, locationManager)
        } else {
            /*
Class GPS initialized
 */
            GPS.liveLocation(this)
        }
    }

    override fun onStart() {
        super.onStart()
        verifyGPSConditions()

    }


}

