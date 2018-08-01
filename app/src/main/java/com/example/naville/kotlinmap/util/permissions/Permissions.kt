package com.example.naville.kotlinmap.util.permissions

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.location.LocationManager
import android.provider.Settings
import android.support.v7.app.AlertDialog
import com.example.naville.kotlinmap.util.geo.location.GPS

class Permissions {

    companion object {

        var proceed = false // button proceed flag
        var activated = false //activated boolean flag for gps enabled/disabled conditions

        /*
         * Verify if GPS is activated
         */
        fun verifyGPS(activity: Activity, locationManager: LocationManager?) {
            if (locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                activated = true
                proceed = true
                println("Verified: $activated")
                GPS.liveLocation(activity)
            } else {
                activated = false
                proceed = false
                println("Verified: $activated")
                showGPSDisabledAlertToUser(activity)
            }
        }

        /*
         * Display message if GPS isn't activated
         */
        private fun showGPSDisabledAlertToUser(activity: Activity) {
            var alertDialogBuilder: AlertDialog.Builder? = null

            if (alertDialogBuilder == null) {

                alertDialogBuilder = AlertDialog.Builder(activity)
                alertDialogBuilder.setMessage("Esse aplicativo necessita do GPS para melhor performance. \n\nPor favor, pressione SIM para ativar o GPS!")
                        .setCancelable(false)
                        .setPositiveButton("Sim") { dialog, _ ->
                            val callGPSSettingIntent = Intent(
                                    Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                            activity.startActivity(callGPSSettingIntent)
                            dialog.dismiss()

                            /*
                             * Initialize live location
                             */
                            GPS.liveLocation(activity)

                            activated = true
                            proceed = true
                            println("ShowAlert: $activated")
                        }
                alertDialogBuilder.setNegativeButton("Não",
                        DialogInterface.OnClickListener { dialog, _ ->
                            activated = false
                            proceed = false
                            println("ShowAlert: $activated")

                            dialog.dismiss()
                        })
                val alert = alertDialogBuilder.create()
                alert.show()
            }
        }

    }

}