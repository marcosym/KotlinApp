package com.example.naville.kotlinmap.util

import android.annotation.SuppressLint
import android.app.Activity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.mapbox.mapboxsdk.geometry.LatLng

@SuppressLint("MissingPermission")
class LiveLocation {

    companion object {

        var currentPosition: LatLng? = null

        fun currentLocation(activity: Activity) {

            val fusedLocationProviderClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity)

            fusedLocationProviderClient.lastLocation.addOnCompleteListener(activity,
                    {
                        it.isComplete
                        it.result

                        currentPosition = LatLng(it.result.latitude, it.result.longitude)
                        println("Current location: " + it.result.latitude + " : " + it.result.longitude)
                        println("Current location (LAT/LNG): " + currentPosition.toString())

                    })
        }
    }
}