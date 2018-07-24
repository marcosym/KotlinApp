package com.example.naville.kotlinmap.util

import android.app.Activity
import android.location.Geocoder
import java.util.*

class Geocoder {

    companion object {

        var addressThoroughfare : String? = null
        var addressLatGeocoded : Double? = null
        var addressLngGeocoded : Double? = null

        fun geocoding(activity: Activity, latitude: Double, longitude: Double) {

            val geocoder = Geocoder(activity, Locale.getDefault())
            val address = geocoder.getFromLocation(latitude, longitude, 1)

            if (address != null) {
                addressThoroughfare = address[0].getAddressLine(0)
                println("Geocoder: $addressThoroughfare")

                reverse(activity)
            }

        }

        private fun reverse(activity: Activity) {

            val geocoder = Geocoder(activity, Locale.getDefault())
            val address = geocoder.getFromLocationName(addressThoroughfare, 1)

            if (address != null) {
                addressLatGeocoded = address[0].latitude
                addressLngGeocoded = address[0].longitude
                println("Geocoder Reverse: $addressLatGeocoded / $addressLngGeocoded")
            }

        }

    }




}