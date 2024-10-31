package it.uninsubria.mybeer.activities

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.ContentValues.TAG
import android.content.pm.PackageManager
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
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.api.net.SearchByTextRequest
import com.google.android.material.floatingactionbutton.FloatingActionButton
import it.uninsubria.mybeer.BuildConfig
import it.uninsubria.mybeer.R
import it.uninsubria.mybeer.datamodel.Beer
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
    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search_breweries)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)){v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
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

    private fun addMarkers(googleMap: GoogleMap){
        val placeFields: List<Place.Field> = listOf(Place.Field.ID, Place.Field.DISPLAY_NAME)
        val searchByTextRequest: SearchByTextRequest = SearchByTextRequest.builder("${selectedBeer.beer_brewery}", placeFields)
            .setMaxResultCount(1).build()
        placesClient.searchByText(searchByTextRequest).addOnSuccessListener{response ->
            val places: List<Place> = response.places
            println(places)
            places.forEach{ place ->
                place.location?.let {
                    MarkerOptions()
                        .title(selectedBeer.beer_brewery)
                        .position(it)
                }?.let {
                    googleMap.addMarker(
                        it
                    )
                }
            }
        }

    }
}