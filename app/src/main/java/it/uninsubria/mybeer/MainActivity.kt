package it.uninsubria.mybeer

import android.content.ContentValues.TAG
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.util.Log
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.navigation.createGraph
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.FirebaseDatabase
import it.uninsubria.mybeer.activities.ReportBeerActivity
import it.uninsubria.mybeer.adapters.BeerListAdapter
import it.uninsubria.mybeer.datamodel.Beer
import it.uninsubria.mybeer.datamodel.Report
import it.uninsubria.mybeer.datamodel.User
import it.uninsubria.mybeer.dbHandler.DatabaseHandler
import it.uninsubria.mybeer.fragments.BeerFragment
import it.uninsubria.mybeer.fragments.VetrinaFragment
import it.uninsubria.mybeer.listeners.BeerClickListener

class MainActivity : AppCompatActivity() {

    private val DATABASE_NAME = "https://mybeer-f68c5-default-rtdb.europe-west1.firebasedatabase.app"
    private lateinit var db: FirebaseDatabase
    private lateinit var floatingActionButton: FloatingActionButton
    private lateinit var sqLiteHandler: DatabaseHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        db = FirebaseDatabase.getInstance(DATABASE_NAME)
        sqLiteHandler = DatabaseHandler(baseContext, db)

        val vetrinaFragment = VetrinaFragment(db, sqLiteHandler)
        val beerFragment = BeerFragment(db, sqLiteHandler)
        setCurrentFragment(vetrinaFragment)

        floatingActionButton = findViewById<FloatingActionButton>(R.id.floating_button)
        val popupMenu = PopupMenu(baseContext, floatingActionButton)
        popupMenu.menuInflater.inflate(R.menu.popup_menu, popupMenu.menu)
        val menuItemCategory = popupMenu.menu.findItem(R.id.fam_item_search_cat)
        val menuItemVetrina = popupMenu.menu.findItem(R.id.fam_item_vetrina)
        val menuItemMaps = popupMenu.menu.findItem(R.id.fam_item_maps)

        val reportBeerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
                result ->
            if(result.resultCode == RESULT_OK){
                Toast.makeText(baseContext, "Report Ok", Toast.LENGTH_LONG).show()
                if(result.data != null){
                    val beerReport = result.data!!.getSerializableExtra("it.uninsubria.mybeer.report") as Report
                    Toast.makeText(baseContext, beerReport.toString(), Toast.LENGTH_LONG).show()
                    sqLiteHandler.addReport(beerReport)
                }
            }else{
                Toast.makeText(baseContext, "Report Ko", Toast.LENGTH_LONG).show()
            }
        }


        floatingActionButton.setOnClickListener{
            popupMenu.setOnMenuItemClickListener {
                    menuItem ->
                // Ricerca per categoria
                if(menuItem.equals(menuItemCategory)){
                    setCurrentFragment(beerFragment)
                }else if(menuItem.equals(menuItemVetrina)){//move to vetrina fragment
                    setCurrentFragment(vetrinaFragment)
                }else if(menuItem.equals(menuItemMaps)){//move to fragment maps
                    Toast.makeText(baseContext, "$menuItem.title", Toast.LENGTH_LONG).show()
                    TODO("maps fragment")
                }
                true
            }
            popupMenu.show()
        }


    }

    private fun setCurrentFragment(fragment: Fragment) = supportFragmentManager.beginTransaction().apply{
        replace(R.id.nav_host_fragment, fragment)
        commit()
    }

}