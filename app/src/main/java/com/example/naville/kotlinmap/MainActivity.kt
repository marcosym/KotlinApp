package com.example.naville.kotlinmap

import android.location.Location
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.EditText
import com.example.naville.kotlinmap.util.GPS
import com.example.naville.kotlinmap.util.Geocoder
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.places.AutocompleteFilter
import com.google.android.gms.location.places.Place
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment
import com.google.android.gms.location.places.ui.PlaceSelectionListener
import com.mapbox.android.core.location.LocationEngine
import com.mapbox.android.core.location.LocationEngineProvider
import com.mapbox.api.directions.v5.models.DirectionsResponse
import com.mapbox.api.directions.v5.models.DirectionsRoute
import com.mapbox.api.directions.v5.models.DirectionsWaypoint
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.annotations.MarkerOptions
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.constants.Style
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncher
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncherOptions
import com.mapbox.services.android.navigation.v5.navigation.MapboxNavigation
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : AppCompatActivity() {

    private lateinit var mapView: MapView
    private var mapboxNavigation: MapboxNavigation? = null
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
        mapView.getMapAsync(
                {
                    it.setStyle(Style.LIGHT)
                    it.easeCamera(CameraUpdateFactory.newLatLngZoom(GPS.currentPosition!!, 16.0))
                    it.uiSettings.setAllGesturesEnabled(true)
                    it.uiSettings.isZoomControlsEnabled
                    it.uiSettings.isZoomGesturesEnabled
                    it.addMarker(MarkerOptions().position(GPS.currentPosition))
                })

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


    }

    /*
     * Create 2 points origin and destination
     */
    private fun createPointForNavigation() {

        originPoint = Point.fromLngLat(selectedLatOrigin!!, selectedLngOrigin!!)
        destPoint = Point.fromLngLat(selectedLatDest!!, selectedLngDest!!)

        println("Points: origin - " + originPoint.latitude() + ":" + originPoint.longitude() + " destination - " + destPoint.latitude() + ":" + destPoint.longitude())

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


        val simulateRoute = true

        val options : NavigationLauncherOptions = NavigationLauncherOptions.builder()
                .shouldSimulateRoute(simulateRoute)
                .build()
        NavigationLauncher.startNavigation(this, options)

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
                        println("Place (Success): $place")
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

//    override fun onPause() {
//        super.onPause()
//        mapView.onPause()
//    }

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
