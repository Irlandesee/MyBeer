package it.uninsubria.mybeer.activities

import android.os.Bundle
import android.widget.EditText
import android.widget.PopupMenu
import android.widget.Spinner
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.FirebaseDatabase
import it.uninsubria.mybeer.R
import it.uninsubria.mybeer.dbHandler.DatabaseHandler

class ReportBeerActivity : AppCompatActivity() {
    private val DATABASE_NAME = "https://mybeer-f68c5-default-rtdb.europe-west1.firebasedatabase.app"
    private lateinit var firebaseDb: FirebaseDatabase
    private lateinit var floatingActionButton: FloatingActionButton
    private lateinit var sqliteHandler: DatabaseHandler

    private lateinit var editBeerName: EditText
    private lateinit var spinnerBeerStyle: Spinner
    private lateinit var editBeerBrewery: EditText

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_report_beer)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)){v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        firebaseDb = FirebaseDatabase.getInstance(DATABASE_NAME)
        sqliteHandler = DatabaseHandler(baseContext, firebaseDb)

        editBeerName = findViewById(R.id.edit_beer_name)
        spinnerBeerStyle = findViewById(R.id.spinner_beer_style)
        editBeerBrewery = findViewById(R.id.edit_beer_brewery)
        floatingActionButton = findViewById(R.id.fam_report_beer)
        val popupMenu = PopupMenu(baseContext, floatingActionButton)
        popupMenu.menuInflater.inflate(R.menu.report_beer_menu, popupMenu.menu)
        val menuItemTakePhoto = popupMenu.menu.findItem(R.id.fam_take_photo)
        val menuItemBack = popupMenu.menu.findItem(R.id.fam_back_main)
        floatingActionButton.setOnClickListener{
            popupMenu.setOnMenuItemClickListener{
                menuItem ->
                    if(menuItem.equals(menuItemTakePhoto)){
                        TODO("Not yet implemented")
                    }else if(menuItem.equals(menuItemBack)){
                        TODO("Not yet implemented")
                    }
                    true
            }
            popupMenu.show()
        }

    }


}