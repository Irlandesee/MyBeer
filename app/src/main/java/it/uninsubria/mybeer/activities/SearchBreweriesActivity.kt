package it.uninsubria.mybeer.activities
import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.OnRequestPermissionsResultCallback
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.Task
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.material.floatingactionbutton.FloatingActionButton
import it.uninsubria.mybeer.BuildConfig
import it.uninsubria.mybeer.R
import it.uninsubria.mybeer.datamodel.Beer
import java.util.Locale

class SearchBreweriesActivity : AppCompatActivity(),
    OnMapReadyCallback,
    OnRequestPermissionsResultCallback{

    private lateinit var editBeerBrewery: EditText
    private lateinit var selectedBeer: Beer
    private val mapsApiKey: String = BuildConfig.MAPS_API_KEY
    private lateinit var placesClient: PlacesClient

    private var permissionDenied: Boolean = false
    private lateinit var googleMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val locationPermissionRequest = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){ permissions ->
        if(permissions.entries.all { it.value }) {
            getLastLocation()
        }
        else{
            Toast.makeText(this, "Location permission denied", Toast.LENGTH_LONG).show()
        }
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

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map_fragment) as? SupportMapFragment
        mapFragment?.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationPermissionRequest.launch(arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION))

        editBeerBrewery = findViewById(R.id.autoCompleteView)
        val extras: Bundle? = intent.extras
        if(extras != null){
            selectedBeer = extras.getSerializable("selected_beer") as Beer
            editBeerBrewery.setText(selectedBeer.beer_brewery)
        }

        val floatingActionButton: FloatingActionButton = findViewById(R.id.floating_button)
        floatingActionButton.setOnClickListener{
            setResult(RESULT_OK)
            finish()
        }

    }

    override fun onResumeFragments() {
        super.onResumeFragments()
        if(permissionDenied) Toast.makeText(this, "Permessi posizione negati", Toast.LENGTH_LONG).show()
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            googleMap.isMyLocationEnabled = true
        }
    }

    private fun getLastLocation(){
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            return
        }
        val locationTask: Task<Location> = fusedLocationClient.lastLocation
        locationTask.addOnSuccessListener{ location: Location? ->
            if(location != null){
                val userLocation = LatLng(location.latitude, location.longitude)
                //googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15f))
                //googleMap.addMarker(MarkerOptions().position(userLocation).title("You are here"))
                selectedBeer.beer_brewery?.let { addPlaceMarker(it) }
            }else{
                Toast.makeText(this, "Luogo non disponibile", Toast.LENGTH_LONG).show()
            }
        }.addOnFailureListener{ e -> Toast.makeText(this, "Failed to get location ${e.message}", Toast.LENGTH_LONG).show()}


    }

    private fun addPlaceMarker(placeName: String){
        val geocoder = Geocoder(this, Locale.getDefault())
        val addresses = geocoder.getFromLocationName(placeName, 1)

        if(!addresses.isNullOrEmpty()){
            val address = addresses[0]
            val placeLocation = LatLng(address.latitude, address.longitude)
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(placeLocation, 15f))
            googleMap.addMarker(MarkerOptions().position(placeLocation).title(placeName))
        }else{
            Toast.makeText(this, "Non è stato possibile trovare: $placeName", Toast.LENGTH_LONG).show()
        }
    }


}