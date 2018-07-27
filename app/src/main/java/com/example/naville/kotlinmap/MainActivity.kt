package com.example.naville.kotlinmap

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.widget.EditText
import com.example.naville.kotlinmap.util.geo.location.GPS
import com.example.naville.kotlinmap.util.geo.location.Geocoder
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.places.AutocompleteFilter
import com.google.android.gms.location.places.Place
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment
import com.google.android.gms.location.places.ui.PlaceSelectionListener
import com.mapbox.android.core.location.LocationEngine
import com.mapbox.android.core.location.LocationEngineListener
import com.mapbox.android.core.location.LocationEnginePriority
import com.mapbox.android.core.location.LocationEngineProvider
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.api.directions.v5.models.DirectionsResponse
import com.mapbox.api.directions.v5.models.DirectionsRoute
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.annotations.Marker
import com.mapbox.mapboxsdk.annotations.MarkerOptions
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.constants.Style
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.plugins.locationlayer.LocationLayerPlugin
import com.mapbox.mapboxsdk.plugins.locationlayer.modes.RenderMode
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncher
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncherOptions
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute
import com.mapbox.services.android.navigation.v5.navigation.MapboxNavigation
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import java.util.*


class MainActivity : AppCompatActivity(), PermissionsListener, LocationEngineListener {

    private var permissionsManager: PermissionsManager? = null
    private var locationPlugin: LocationLayerPlugin? = null
    private var locationEngine: LocationEngine? = null
    private var originLocation: Location? = null

    private var currentRoute: DirectionsRoute? = null
    private var navigationMapRoute: NavigationMapRoute? = null

    private lateinit var mapView: MapView
    private var mapboxNavigation: MapboxNavigation? = null
    private lateinit var mapBox: MapboxMap

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

            /*
             * Enabling the location plugin
             */
            enableLocationPlugin()

            mapBox.setStyle(Style.LIGHT)


            val cal = Calendar.getInstance()

            val hourOfDay = cal.get(Calendar.HOUR_OF_DAY)
            val minutes = cal.get(Calendar.MINUTE)
            val seconds = cal.get(Calendar.SECOND)

            if (hourOfDay == 6 && minutes == 0 && seconds == 0) {
                mapView.setStyleUrl(Style.LIGHT)
            } else if (hourOfDay == 18 && minutes == 0 && seconds == 0) {
                mapView.setStyleUrl(Style.DARK)
            }
            mapBox.easeCamera(CameraUpdateFactory.newLatLngZoom(GPS.currentPosition!!, 16.0))
            mapBox.uiSettings.setAllGesturesEnabled(true)
            mapBox.uiSettings.isZoomControlsEnabled
            mapBox.uiSettings.isZoomGesturesEnabled
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

    private fun enableLocationPlugin() {

        if (PermissionsManager.areLocationPermissionsGranted(this)) {
            initializeLocationEngine()

            locationPlugin = LocationLayerPlugin(mapView, mapBox, locationEngine)
            locationPlugin!!.renderMode = RenderMode.COMPASS
        } else {
            permissionsManager = PermissionsManager(this)
            permissionsManager!!.requestLocationPermissions(this)

        }

    }

    @SuppressLint("MissingPermission")
    private fun initializeLocationEngine() {
        val locationEngineProvider = LocationEngineProvider(this)
        locationEngine = locationEngineProvider.obtainBestLocationEngineAvailable()
        locationEngine!!.priority = LocationEnginePriority.HIGH_ACCURACY
        locationEngine!!.activate()

        val lastLocation: Location = locationEngine!!.lastLocation
        originLocation = lastLocation
        setCameraPosition(lastLocation)
    }

    private fun setCameraPosition(location: Location) {
        mapBox.animateCamera(CameraUpdateFactory.newLatLngZoom(
                LatLng(location.latitude, location.longitude), 13.0))
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
                        /*
                         * Handle the map style according to the current time
                         */
                        handlingMapStyle()

//                        onChangingPosition(GPS.currentPosition!!)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
        timer.schedule(timerTask, 5, 1000)
    }

    @SuppressLint("SimpleDateFormat")
    /*
     * It changes the map style according the time - 6am - DAY STYLE / 6pm - NIGHT STYLE
     */
    private fun handlingMapStyle() {

        val cal = Calendar.getInstance()

        val hourOfDay = cal.get(Calendar.HOUR_OF_DAY)
        val minutes = cal.get(Calendar.MINUTE)
        val seconds = cal.get(Calendar.SECOND)

        println("Horário: $hourOfDay:$minutes:$seconds")

        if (hourOfDay == 6 && minutes == 0 && seconds == 0) {
            mapView.setStyleUrl(Style.LIGHT)
        } else if (hourOfDay == 18 && minutes == 0 && seconds == 0) {
            mapView.setStyleUrl(Style.DARK)
        }

    }

    /*
     * If users location change the new position will be added with a marker
     */
    fun onChangingPosition(latLng: LatLng) {

        val latLngLastPos: LatLng?
        latLngLastPos = LatLng(latLng)

        println("Última posição $latLngLastPos")

        if (markerMyLocation != null) {
            mapBox.removeMarker(markerMyLocation!!)
            markerMyLocation!!.position.latitude = latLng.latitude
            markerMyLocation!!.position.longitude = latLng.longitude

        }

        markerMyLocation = mapBox.addMarker(MarkerOptions()
                .position(latLng)
                .title("Sua localização!"))
        Geocoder.geocoding(this, latLng.latitude, latLng.longitude)
        searchOrigin!!.setText(Geocoder.addressThoroughfare)
    }

    /*
    * Create 2 points origin and destination
    */
    private fun createPointForNavigation() {

//        originPoint = Point.fromLngLat(selectedLatOrigin!!, selectedLngOrigin!!)
//        destPoint = Point.fromLngLat(selectedLatDest!!, selectedLngDest!!)

        originPoint = Point.fromLngLat(-46.5287985, -23.4675446)
        destPoint = Point.fromLngLat(-46.5312937,-23.4665668)

        println("Points: origin = " + originPoint.latitude() + ":" + originPoint.longitude()
                + " destination = " + destPoint.latitude() + ":" + destPoint.longitude())

        getRoute(originPoint, destPoint)

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
    @SuppressLint("MissingPermission")
    override fun onStart() {
        super.onStart()

        if (locationEngine != null) {
            locationEngine!!.requestLocationUpdates()
        }
        if (locationPlugin != null) {
            locationPlugin!!.onStart()
        }

        mapView.onStart()

    }

    override fun onStop() {
        super.onStop()

        if (locationEngine != null) {
            locationEngine!!.removeLocationUpdates()
        }
        if (locationPlugin != null) {
            locationPlugin!!.onStop()
        }


        mapView.onStop()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)


    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()

        if (locationEngine != null) {
            locationEngine!!.deactivate()
        }


    }

    override fun onExplanationNeeded(permissionsToExplain: MutableList<String>?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onPermissionResult(granted: Boolean) {
        if (granted) {
            enableLocationPlugin()
        } else {
            finish()
        }
    }

    override fun onLocationChanged(location: Location?) {
        if (location != null) {
            originLocation = location
            setCameraPosition(location)
            locationEngine!!.removeLocationEngineListener(this)
        }
    }

    @SuppressLint("MissingPermission")
    override fun onConnected() {
        locationEngine!!.requestLocationUpdates()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        permissionsManager!!.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun getRoute(origin: Point, destination: Point) {

        Timber.d("ENTROU")

        NavigationRoute.builder(this)
                .accessToken(getString(R.string.mapboxKey))
                .origin(origin)
                .destination(destination)
                .build()
                .getRoute(object : Callback<DirectionsResponse> {

                    override fun onFailure(call: Call<DirectionsResponse>?, t: Throwable?) {
                        Timber.e("DirectionsResponse Error: ${t!!.localizedMessage}")
                    }

                    override fun onResponse(call: Call<DirectionsResponse>?, response: Response<DirectionsResponse>?) {
                        // You can get the generic HTTP info about the response
                        Timber.d("Response code: ${response!!.code()}")

                        if (response.body() == null) {
                            Timber.e("No routes found, make sure you set the right user and access token.")
                            return
                        } else if (response.body()!!.routes().size < 1) {
                            Timber.e("No routes found")
                            return
                        }

                        currentRoute = response.body()!!.routes()[0]

                        // Draw the route on the map
                        if (navigationMapRoute != null) {
                            navigationMapRoute!!.removeRoute()
                        } else {
                            navigationMapRoute = NavigationMapRoute(null, mapView, mapBox, R.style.NavigationMapRoute)
                        }
                        navigationMapRoute!!.addRoute(currentRoute)
                    }
                })

        val handler = Handler()
        handler.postDelayed({

            val options: NavigationLauncherOptions = NavigationLauncherOptions.builder()
                    .directionsRoute(currentRoute)
                    .shouldSimulateRoute(true)
                    .build()

            // Call this method with Context from within an Activity
            NavigationLauncher.startNavigation(this, options)

        }, 3000)

    }
}