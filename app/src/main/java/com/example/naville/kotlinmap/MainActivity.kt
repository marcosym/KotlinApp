package com.example.naville.kotlinmap

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.example.naville.kotlinmap.util.LiveLocation
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.constants.Style
import com.mapbox.mapboxsdk.maps.MapView

class MainActivity : AppCompatActivity() {

    /*
     * Mapview from mapbox
     */
    lateinit var mapView: MapView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /*
         * Gets the key
         */
        Mapbox.getInstance(applicationContext, getString(R.string.mapboxKey))

        /*
         * Set the main layout
         */
        setContentView(R.layout.activity_main)

        /*
         * Initialize and setup the map
         */
        mapView = findViewById(R.id.map)

        /*
         * Create the mapbox and configs
         */
        mapView.onCreate(savedInstanceState)

        mapView.getMapAsync(
                {
                    it.setStyle(Style.SATELLITE_STREETS)
                    it.easeCamera(CameraUpdateFactory.newLatLngZoom(LiveLocation.currentPosition!!, 17.0))
                    it.uiSettings.setAllGesturesEnabled(true)
                    it.uiSettings.isZoomControlsEnabled
                    it.uiSettings.isZoomGesturesEnabled

//                    it.addMarker(MarkerOptions().position(LiveLocation.currentPosition))
                })



    }


    /*
    Android lifecycle
     */

    override fun onStart() {
        super.onStart()
        mapView.onStart()

        /*
        * Get permissions for location
        */
//        Permissions.verifyGPS(this)

        /*
Class LIVE LOCATION initialized
 */
//        LiveLocation.currentLocation(this)]
        LiveLocation.updatedLocation(this)


    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)


    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

}
