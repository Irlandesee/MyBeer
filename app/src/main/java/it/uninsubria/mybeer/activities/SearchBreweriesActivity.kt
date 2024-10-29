package it.uninsubria.mybeer.activities

import android.os.Bundle
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.setPadding
import com.google.android.material.floatingactionbutton.FloatingActionButton
import it.uninsubria.mybeer.R
import it.uninsubria.mybeer.datamodel.Beer

class SearchBreweriesActivity : AppCompatActivity(){

    private lateinit var editBeerBrewery: EditText
    private lateinit var selectedBeer: Beer

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search_breweries)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)){v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

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
}