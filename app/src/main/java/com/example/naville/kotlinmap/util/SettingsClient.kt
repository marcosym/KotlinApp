package com.example.naville.kotlinmap.util

import android.app.Activity
import android.content.IntentSender
import com.example.naville.kotlinmap.util.geo.location.GPS
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task

class SettingsClient {

    companion object {

        var REQUEST_CHECK_SETTINGS: Int = 1

        /*
         * SettingsClient GPS to enable GPS without INTENT
         */
        fun settingsClientConfig(activity: Activity) {

            val locationRequest = LocationRequest()
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                    .setInterval(1000)
                    .setFastestInterval(1000)

            val builder: LocationSettingsRequest.Builder = LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest)
                    .setNeedBle(true)

            val result: Task<LocationSettingsResponse>? = LocationServices.getSettingsClient(activity).checkLocationSettings(builder.build())

            result!!.addOnCompleteListener {
                try {
                    val response = it.getResult(ApiException::class.java)
                    GPS.liveLocation(activity)

                    print("Response: $response")
                } catch (exception: ApiException) {
                    when (exception.statusCode) {
                        LocationSettingsStatusCodes.RESOLUTION_REQUIRED ->
                            try {
                                val resolvable = exception as ResolvableApiException
                                resolvable.startResolutionForResult(activity, REQUEST_CHECK_SETTINGS)

                            } catch (e: IntentSender.SendIntentException) {
                                print("SendIntentException: ${e.localizedMessage}")
                            } catch (e: ClassCastException) {
                                print("ClassCastException: ${e.localizedMessage}")
                            }
                    }
                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE
                }
            }

        }
    }
}
