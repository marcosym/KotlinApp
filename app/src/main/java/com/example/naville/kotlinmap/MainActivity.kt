package com.example.naville.kotlinmap

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.EditText
import com.example.naville.kotlinmap.util.GPS
import com.example.naville.kotlinmap.util.Geocoder
import com.google.android.gms.location.places.AutocompleteFilter
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.constants.Style
import com.mapbox.mapboxsdk.maps.MapView


abstract class MainActivity : AppCompatActivity() {

    private lateinit var mapView: MapView
//    private var mapboxNavigation: MapboxNavigation? = null
    var searchOrigin: PlaceAutocompleteFragment? = null
    var searchDestination: PlaceAutocompleteFragment? = null
    private lateinit var originPoint: Point
    private lateinit var destPoint: Point


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /*
         * Gets the keys
         */
        Mapbox.getInstance(applicationContext, getString(R.string.mapboxKey))
//        mapboxNavigation = MapboxNavigation(this, getString(R.string.mapboxKey))

        /*
* Calls GEOCODER Class to convert places
*/
        Geocoder.geocoding(this, GPS.currentPosition!!.latitude, GPS.currentPosition!!.longitude)

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

        /*
         * Open map
         */
        mapView.getMapAsync(
                {
                    it.setStyle(Style.LIGHT)
                    it.easeCamera(CameraUpdateFactory.newLatLngZoom(GPS.currentPosition!!, 16.0))
                    it.uiSettings.setAllGesturesEnabled(true)
                    it.uiSettings.isZoomControlsEnabled
                    it.uiSettings.isZoomGesturesEnabled
0                })

        /*
         * Finding views by ID
         */
        findViews()

        /*
         * Setting up the configs
         */
        setConfigAutoComplete(searchOrigin, searchDestination)

        /*
  * Init actions of Main Activity
  */
        initActions()

    }

    private fun initActions() {

        /*
         * Creates lat lng points for navigation GPS mapbox
         */
//        createPointForNavigation()


    }


    /*
     * Setting up filters and configs inside place autocomplete fragment
     */
    private fun setConfigAutoComplete(origin: PlaceAutocompleteFragment?, dest: PlaceAutocompleteFragment?) {

        val typeFilter = AutocompleteFilter.Builder()
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_NONE)
                .setCountry("BR")
                .build()

        /*
         * BR places only
         */
        origin!!.setFilter(typeFilter)
        dest!!.setFilter(typeFilter)

        /*
         * Hint text
         */
        origin.setHint("Partida")
        dest.setHint("Destino")

        /*
         * Setting up size
         */
        (origin.view!!.findViewById(R.id.place_autocomplete_search_input) as EditText).textSize = 15.0f
        (dest.view!!.findViewById(R.id.place_autocomplete_search_input) as EditText).textSize = 15.0f

        origin.setText(Geocoder.addressThoroughfare)

    }

    /*
     * Finding views
     */
    private fun findViews() {

        searchOrigin = fragmentManager.findFragmentById(R.id.searchOrigin) as PlaceAutocompleteFragment
        searchDestination = fragmentManager.findFragmentById(R.id.searchDestination) as PlaceAutocompleteFragment
    }

    /*
    Android lifecycle
     */
    override fun onStart() {
        super.onStart()
        mapView.onStart()


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
