package it.uninsubria.mybeer.activities

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.maps.GoogleMap
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

class SearchBreweriesActivity : AppCompatActivity(){

    private lateinit var editBeerBrewery: EditText
    private lateinit var selectedBeer: Beer
    private val placesApiKey: String = BuildConfig.PLACES_API_KEY
    private lateinit var placesClient: PlacesClient

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search_breweries)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)){v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        Places.initializeWithNewPlacesApiEnabled(applicationContext, placesApiKey)
        placesClient = Places.createClient(this)

        editBeerBrewery = findViewById(R.id.autoCompleteView)
        val extras: Bundle? = intent.extras
        if(extras != null){
            selectedBeer = extras.getSerializable("selected_beer") as Beer
            editBeerBrewery.setText(selectedBeer.beer_brewery)
        }

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map_fragment) as? SupportMapFragment
        mapFragment?.getMapAsync{ googleMap ->
            addMarkers(googleMap)
        }

        val floatingActionButton: FloatingActionButton = findViewById(R.id.floating_button)
        floatingActionButton.setOnClickListener{
            setResult(RESULT_OK)
            finish()
        }

    }

    private fun addMarkers(googleMap: GoogleMap){
        val placeFields: List<Place.Field> = listOf(Place.Field.ID, Place.Field.DISPLAY_NAME)
        val searchByTextRequest: SearchByTextRequest = SearchByTextRequest.builder("${selectedBeer.beer_brewery}", placeFields)
            .setMaxResultCount(1).build()
        placesClient.searchByText(searchByTextRequest).addOnSuccessListener{response ->
            val places: List<Place> = response.places
            Log.w(TAG, places.toString())
        }
        /**
        googleMap.addMarker(MarkerOptions()
            .title(selectedBeer.beer_brewery)
            .position()
        )
        **/

    }
}