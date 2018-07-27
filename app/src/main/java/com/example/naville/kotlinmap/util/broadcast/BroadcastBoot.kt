package com.example.naville.kotlinmap.util.broadcast

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import com.example.naville.kotlinmap.util.geo.location.GPS

class BroadcastBoot : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {

        val locationManager = context!!.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        val gpsActivated = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val networkActivated = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

        if (gpsActivated && networkActivated) {
            GPS.liveLocation(context as Activity)
            println("GPS e INTERNET ATIVADOS")
        }
    }


}

