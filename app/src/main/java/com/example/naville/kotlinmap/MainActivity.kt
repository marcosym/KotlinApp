package com.example.naville.kotlinmap

import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.widget.EditText
import com.example.naville.kotlinmap.util.location.GPS
import com.example.naville.kotlinmap.util.location.Geocoder
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.places.AutocompleteFilter
import com.google.android.gms.location.places.Place
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment
import com.google.android.gms.location.places.ui.PlaceSelectionListener
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.annotations.Marker
import com.mapbox.mapboxsdk.annotations.MarkerOptions
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.constants.Style
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.services.android.navigation.v5.navigation.MapboxNavigation
import java.util.*


class MainActivity : AppCompatActivity() {


    private lateinit var mapView: MapView
    private var mapboxNavigation: MapboxNavigation? = null
    private var mapBox: MapboxMap? = null

    var searchOrigin: PlaceAutocompleteFragment? = null
    var searchDestination: PlaceAutocompleteFragment? = null

    private lateinit var originPoint: Point
    private lateinit var destPoint: Point

    private var selectedOrigin: String? = null
    private var selectedDest: String? = null
    private var selectedLatOrigin: Double? = null
    private var selectedLngOrigin: Double? = null
    private var selectedLatDest: Double? = null
    private var selectedLngDest: Double? = null

    private var timerTask: TimerTask? = null
    private var markerMyLocation: Marker? = null




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /*
         * Gets the keys
         */
        Mapbox.getInstance(applicationContext, getString(R.string.mapboxKey))
        mapboxNavigation = MapboxNavigation(this, getString(R.string.mapboxKey))

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
        mapView.getMapAsync {

            /*
             * mapbox object receives it component
             */

            mapBox = it

            mapBox!!.setStyle(Style.TRAFFIC_DAY)
            mapBox!!.easeCamera(CameraUpdateFactory.newLatLngZoom(GPS.currentPosition!!, 16.0))
            mapBox!!.uiSettings.setAllGesturesEnabled(true)
            mapBox!!.uiSettings.isZoomControlsEnabled
            mapBox!!.uiSettings.isZoomGesturesEnabled
        }

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


    /*
     * Init actions on Main Activity
     */
    private fun initActions() {

        /*
         * AsyncTask repeating- it clears and creates a new marker if position be changed
         */
        val handler = Handler()
        val timer = Timer()

        timerTask = object : TimerTask() {
            override fun run() {
                handler.post {
                    try {
                        onChangingPosition(GPS.currentPosition!!)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
        timer.schedule(timerTask, 5, 1000)
    }

    /*
     * If users location change the new position will be added with a marker
     */
    fun onChangingPosition(latLng: LatLng) {

        val latLngLastPos: LatLng?
        latLngLastPos = LatLng(latLng)

        println("Última posição $latLngLastPos")

        if (markerMyLocation != null) {
            mapBox!!.removeMarker(markerMyLocation!!)
            markerMyLocation!!.position.latitude = latLng.latitude
            markerMyLocation!!.position.longitude = latLng.longitude

        }
        if (mapBox != null) {

            markerMyLocation = mapBox!!.addMarker(MarkerOptions()
                    .position(latLng)
                    .title("Sua localização!"))
            Geocoder.geocoding(this, latLng.latitude, latLng.longitude)
            searchOrigin!!.setText(Geocoder.addressThoroughfare)
        }
    }

    /*
    * Create 2 points origin and destination
    */
    private fun createPointForNavigation() {

        originPoint = Point.fromLngLat(selectedLatOrigin!!, selectedLngOrigin!!)
        destPoint = Point.fromLngLat(selectedLatDest!!, selectedLngDest!!)

        println("Points: origin = " + originPoint.latitude() + ":" + originPoint.longitude() + " destination = " + destPoint.latitude() + ":" + destPoint.longitude())

//        NavigationRoute.builder(applicationContext)
//                .accessToken(Mapbox.getAccessToken()!!)
//                .origin(originPoint)
//                .destination(destPoint)
//                .build()
//
//        val location: LocationEngine = LocationEngineProvider(applicationContext).obtainBestLocationEngineAvailable()
//        mapboxNavigation!!.locationEngine = location
//
//        mapboxNavigation!!.addNavigationEventListener {
//            println("Navigation Listener: $it")
//        }


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
        selectedOrigin = Geocoder.addressThoroughfare
        selectedLatOrigin = Geocoder.addressLatGeocoded
        selectedLngOrigin = Geocoder.addressLngGeocoded


        /*
         * LISTENERS
         * ---------------------------------------
         * PlaceautocompleteFragment - ORIGIN
         * PlaceautocompleteFragment - DESTINATION
         *
         */

        origin.setOnPlaceSelectedListener(
                object : PlaceSelectionListener {
                    override fun onError(error: Status?) {
                        println("Place (Error): $error")
                    }

                    override fun onPlaceSelected(place: Place?) {
                        println("Place (Success): $place")
                        selectedOrigin = place!!.address.toString()
                        println("Address origin: $selectedOrigin")
                    }
                })

        dest.setOnPlaceSelectedListener(
                object : PlaceSelectionListener {
                    override fun onError(error: Status?) {
                        println("Place (Error): $error")
                    }

                    override fun onPlaceSelected(place: Place?) {
                        selectedDest = place!!.address.toString()
                        println("Address destination: $selectedDest")
                        Geocoder.geocoding(this@MainActivity, place.latLng.latitude, place.latLng.longitude)
                        selectedDest = Geocoder.addressThoroughfare
                        selectedLatDest = Geocoder.addressLatGeocoded
                        selectedLngDest = Geocoder.addressLngGeocoded
                        println("Address destination: $selectedDest")

                        /*
      * Creates lat lng points for navigation GPS mapbox
      */
                        createPointForNavigation()

                    }
                })

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
        timerTask!!.cancel()
    }

}
