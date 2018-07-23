package com.example.naville.kotlinmap.util

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.location.LocationManager
import android.provider.Settings
import android.support.v7.app.AlertDialog

class Permissions {

    companion object {

        var activated = true //ativou boolean flag for gps enabled/disabled conditions
        var locationManager : LocationManager? = null //locationManager handle with GPS condition, recognize if its off/on

        /*
         * Verify if GPS is activated
         */
        fun verifyGPS(activity: Activity) {
            if (locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                activated = true
                println("Verified: $activated")
            } else {
                activated = false
                println("Verified: $activated")
                showGPSDisabledAlertToUser(activity)
            }
        }

        /*
         * Display message if GPS isn't activated
         */
        fun showGPSDisabledAlertToUser(activity: Activity) {
            var alertDialogBuilder: AlertDialog.Builder? = null

            if (alertDialogBuilder == null) {

                alertDialogBuilder = AlertDialog.Builder(activity)
                alertDialogBuilder.setMessage("Seu GPS está desativado... Gostaria de ativá-lo?")
                        .setCancelable(false)
                        .setPositiveButton("Sim") { dialog, _ ->
                            val callGPSSettingIntent = Intent(
                                    Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                            activity.startActivity(callGPSSettingIntent)
                            dialog.dismiss()

//                        LiveLocation.currentLocation(activity)
//                        ToolboxGetLocation.getLastLocation(activity)
//                        ToolboxGetLocation.getUpdatedLocation(activity)

                            activated = true
                            println("ShowAlert: $activated")
                        }
                alertDialogBuilder.setNegativeButton("Não",
                        DialogInterface.OnClickListener { dialog, _ ->
                            activated = false
                            dialog.dismiss()
                        })
                val alert = alertDialogBuilder.create()
                alert.show()
            }
        }

    }

}