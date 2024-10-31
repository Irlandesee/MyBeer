package it.uninsubria.mybeer.activities

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.OnRequestPermissionsResultCallback
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.location.Priority
import com.google.android.gms.location.SettingsClient
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.Task
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.CircularBounds
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.kotlin.circularBounds
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.api.net.SearchByTextRequest
import com.google.android.libraries.places.api.net.SearchNearbyRequest
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.squareup.okhttp.Dispatcher
import it.uninsubria.mybeer.BuildConfig
import it.uninsubria.mybeer.R
import it.uninsubria.mybeer.datamodel.Beer
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Arrays

class SearchBreweriesActivity : AppCompatActivity(),
    OnMapReadyCallback,
    OnRequestPermissionsResultCallback{

    private lateinit var editBeerBrewery: EditText
    private lateinit var selectedBeer: Beer
    private val mapsApiKey: String = BuildConfig.MAPS_API_KEY
    private lateinit var placesClient: PlacesClient

    private var permissionDenied: Boolean = false
    private lateinit var map: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var lastKnowLocation: Location

    //Defines a list of fields to include in the response fo each returned place
    private val placeFields: List<Place.Field> = listOf(Place.Field.ID, Place.Field.DISPLAY_NAME)
    //Defines a list of types to include
    private val includedTypes: List<String> = listOf("pub", "bar")
    private lateinit var bounds: CircularBounds

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)

        lifecycleScope.launch{
            getCurrentLocation()
        }

        enableEdgeToEdge()
        setContentView(R.layout.activity_search_breweries)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)){v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        Places.initializeWithNewPlacesApiEnabled(applicationContext, mapsApiKey)
        placesClient = Places.createClient(this)

        editBeerBrewery = findViewById(R.id.autoCompleteView)
        val extras: Bundle? = intent.extras
        if(extras != null){
            selectedBeer = extras.getSerializable("selected_beer") as Beer
            editBeerBrewery.setText(selectedBeer.beer_brewery)
        }
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)


        val floatingActionButton: FloatingActionButton = findViewById(R.id.floating_button)
        floatingActionButton.setOnClickListener{
            setResult(RESULT_OK)
            finish()
        }

    }

    override fun onResumeFragments() {
        super.onResumeFragments()
        if(permissionDenied) Toast.makeText(this, "Location permission denied", Toast.LENGTH_LONG).show()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        this.map = googleMap
        enableMyLocation()
        val radius: Double = 10000.0 //10 km
        setSearchBounds(radius)
        val searchNearbyRequest: SearchNearbyRequest = SearchNearbyRequest
            .builder(bounds, placeFields)
            .setIncludedTypes(includedTypes)
            .setMaxResultCount(10).build()
        placesClient.searchNearby(searchNearbyRequest)
            .addOnSuccessListener { response ->
                val places: List<Place> = response.places
                Log.w(TAG, "Places: $places")
            }
    }

    private fun enableMyLocation(){
        if(ContextCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION)
            == PackageManager.PERMISSION_GRANTED){
            map.isMyLocationEnabled = true
            return
        }else{
            ActivityCompat.requestPermissions(
                this,
                arrayOf(ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray){
        if(requestCode != LOCATION_PERMISSION_REQUEST_CODE){
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            return
        }
        if(isPermissionGranted(permissions, grantResults, ACCESS_FINE_LOCATION)
            || isPermissionGranted(permissions, grantResults, ACCESS_COARSE_LOCATION)){
            enableMyLocation()
        }else{ permissionDenied = true }

    }

    private fun isPermissionGranted(grantPermissions: Array<String>, grantResults: IntArray, permission: String): Boolean{
        for(i in grantPermissions.indices)
            if(permission == grantPermissions[i])
                return grantResults[i] == PackageManager.PERMISSION_GRANTED
        return false
    }


    @SuppressLint("MissingPermission")
    private suspend fun getCurrentLocation(){
        val result = fusedLocationClient
            .getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, CancellationTokenSource().token)
            .await()
        result?.let{ fetchedLocation -> this.lastKnowLocation = fetchedLocation }
        fusedLocationClient.lastLocation.addOnFailureListener{
            Log.w(TAG, "Failed to retrieve current location")
            //Toast.makeText(this, "Failed to retrive current location", Toast.LENGTH_LONG).show()
        }
    }

    private fun setSearchBounds(radius: Double) {
        val center: LatLng = LatLng(lastKnowLocation.latitude, lastKnowLocation.latitude)
        bounds = CircularBounds.newInstance(center, radius)
    }


    private fun addMarkers(googleMap: GoogleMap){

    }
}