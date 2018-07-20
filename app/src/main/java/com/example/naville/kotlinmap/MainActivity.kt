package com.example.naville.kotlinmap

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.example.naville.kotlinmap.util.LiveLocation
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.annotations.MarkerOptions
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.constants.Style
import com.mapbox.mapboxsdk.maps.MapView

class MainActivity : AppCompatActivity() {

    private lateinit var mapView: MapView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        LiveLocation.currentLocation(this)
        Mapbox.getInstance(applicationContext, getString(R.string.mapboxKey))

        setContentView(R.layout.activity_main)

        mapView = findViewById(R.id.map)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(
                {
                    it.setStyle(Style.MAPBOX_STREETS)
                    it.easeCamera(CameraUpdateFactory.newLatLngZoom(LiveLocation.currentPosition!!, 17.5))
                    it.uiSettings.setAllGesturesEnabled(true)
                    it.uiSettings.isZoomControlsEnabled
                    it.uiSettings.isZoomGesturesEnabled
                    it.addMarker(MarkerOptions().position(LiveLocation.currentPosition))
                })

    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
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

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }


}
