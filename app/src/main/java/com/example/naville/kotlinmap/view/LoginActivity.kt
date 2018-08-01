package com.example.naville.kotlinmap.view

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.location.LocationManager
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.design.widget.TextInputEditText
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.TextView
import com.example.naville.kotlinmap.HandleActivity
import com.example.naville.kotlinmap.R
import com.example.naville.kotlinmap.util.permissions.SettingsClient
import com.example.naville.kotlinmap.util.broadcast.Broadcast
import com.example.naville.kotlinmap.util.fonts.Fonts
import com.example.naville.kotlinmap.util.geo.location.GPS
import pl.droidsonroids.gif.GifDrawable


class LoginActivity : AppCompatActivity() {

    var signInLabel: TextView? = null
    var emailLabel: TextInputEditText? = null
    var passwordLabel: TextInputEditText? = null
    var btnProceed: Button? = null
    var rootConstraintLayout: ConstraintLayout? = null
//    var animationDrawable : AnimationDrawable? = null
//    private var gifBackground: GifDrawable? = null

    private var locationManager: LocationManager? = null
    private var broadcast: Broadcast? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login2)


        locationManager = getSystemService(Context.LOCATION_SERVICE) as? LocationManager
        broadcast = Broadcast(this)

        findViews()
        applyCustomFont()
        initActions()


        /*
         * Animation drawable doing the animation in the background
         */
//        animationDrawable = rootConstraintLayout!!.background as AnimationDrawable
//        animationDrawable!!.setEnterFadeDuration(2000)
//        animationDrawable!!.setExitFadeDuration(4000)
//        animationDrawable!!.start()

    }

    private fun initActions() {

        /*
         * Button proceed action listener
         */
        btnProceed!!.setOnClickListener {
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
//        rootConstraintLayout = findViewById(R.id.rootConstraintLayout)
    }

    /*
     * Verify GPS conditions before start another activity
     */
    private fun verifyGPSConditions() {

        /*
* Get permissions for location
*/
        if (locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            GPS.liveLocation(this)
        } else {
            /*
   * Initialize broadcast: GPS
   */
            broadcast!!.initBroadcastGPS()
            broadcast!!.startBroadcast()
        }


        //        if (!activated) {
//            assert(locationManager != null)
//            Permissions.verifyGPS(this, locationManager)
//        } else {
//            /*
//Class GPS initialized
// */
//            GPS.liveLocation(this)
//        }

    }

    /*
     * Activity on Result handle intent data
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

//        val states: LocationSettingsStates = LocationSettingsStates.fromIntent(data)
//
//        if (states.isGpsPresent) {
//            GPS.liveLocation(this)
//            proceed = true
//        }

        GPS.liveLocation(this)
        if (requestCode == SettingsClient.REQUEST_CHECK_SETTINGS) {
            Activity.RESULT_OK
        } else {
            Activity.RESULT_CANCELED
        }

        broadcast!!.openDialog = true
    }

    override fun onStart() {
        super.onStart()
        verifyGPSConditions()
    }

    override fun onDestroy() {
        super.onDestroy()

        /*
         * Destroy broadcast receiver
         */
        broadcast!!.destroyBroadcast()
    }


}





