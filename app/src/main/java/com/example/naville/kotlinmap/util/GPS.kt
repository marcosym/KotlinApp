package com.example.naville.kotlinmap.util

import android.annotation.SuppressLint
import android.app.Activity
import android.util.Log
import com.example.naville.kotlinmap.MainActivity
import com.google.android.gms.location.*
import com.mapbox.mapboxsdk.geometry.LatLng

@SuppressLint("MissingPermission")
class GPS {

    /*
     * Companion object = Static
     */
    companion object {

        /*
         * LatLng object : currentPosition (get your current position)
         * LocationRequest object: it requests the current location non-stop
         */
        var currentPosition: LatLng? = null
        var locationRequest: LocationRequest? = null

        /*
         * Get user's current location through FusedLocationClient
         */
        fun liveLocation(activity: Activity) {

            val fusedLocationProviderClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity)

            locationRequest = LocationRequest()
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                    .setInterval(1000)
                    .setFastestInterval(1000)

            fusedLocationProviderClient.requestLocationUpdates(locationRequest,
                    object : LocationCallback() {
                        override fun onLocationResult(locationResult: LocationResult?) {
                            for (location in locationResult!!.locations) {
                                currentPosition = LatLng(location.latitude, location.longitude)


                                Log.i("Current location: ", currentPosition.toString())
                            }
                        }
                    }, null)
        }
    }


}

