package com.example.naville.kotlinmap.util.broadcast

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.LocationManager
import com.example.naville.kotlinmap.util.permissions.SettingsClient.Companion.settingsClientConfig
import com.example.naville.kotlinmap.util.geo.location.GPS

data class Broadcast(val activity: Activity) {

    private var intentFilter: IntentFilter? = null
    private var broadcastReceiver: BroadcastReceiver? = null

    var openDialog: Boolean = true

    fun initBroadcastGPS() {
        intentFilter = null
        intentFilter = IntentFilter()
        intentFilter!!.addAction(LocationManager.PROVIDERS_CHANGED_ACTION)

        broadcastReceiver = null
        broadcastReceiver = object : BroadcastReceiver() {

            override fun onReceive(context: Context, intent: Intent) {

                print("INIT ON RECEIVE")

                val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

                val gpsActivated = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                val networkActivated = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

                if (gpsActivated && networkActivated) {
                    GPS.liveLocation(activity)
                    println("GPS e INTERNET ATIVADOS")
                } else {
                    if (openDialog) {
                        openDialog = false
                        settingsClientConfig(activity)
                        print("MANDA PRA ATIVAR")
                    }
                }
            }
        }
    }

    fun startBroadcast() {
        activity.registerReceiver(broadcastReceiver, intentFilter)
        println("START BROADCAST")
    }

    fun destroyBroadcast() {
        if (broadcastReceiver != null) {
            activity.unregisterReceiver(broadcastReceiver)
            println("DESTROY BROADCAST")
        }
    }


}