package it.uninsubria.mybeer.activities

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.FirebaseDatabase
import it.uninsubria.mybeer.R
import it.uninsubria.mybeer.dbHandler.DatabaseHandler
import it.uninsubria.mybeer.fragments.BeerFragment
import it.uninsubria.mybeer.fragments.ReportFragment
import it.uninsubria.mybeer.fragments.VetrinaFragment

class BeerActivity : AppCompatActivity(){
    private val DATABASE_NAME = "https://mybeer-f68c5-default-rtdb.europe-west1.firebasedatabase.app"
    private lateinit var db: FirebaseDatabase
    private lateinit var floatingActionButton: FloatingActionButton
    private lateinit var sqLiteHandler: DatabaseHandler

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.beer_activity)
        db = FirebaseDatabase.getInstance(DATABASE_NAME)
        sqLiteHandler = DatabaseHandler(baseContext, db)
        val available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this)
        if (available == ConnectionResult.SUCCESS){
            Log.d(TAG, "isServicesOk: Google Play Services is working")
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            Log.d(TAG, "Error occured, isResolvable: $available")
        }else{
            Log.d(TAG, "Cant make requests")
            finish()
        }
        val vetrinaFragment = VetrinaFragment(db, sqLiteHandler)
        val beerFragment = BeerFragment(db, sqLiteHandler)
        val reportsFragment = ReportFragment(db, sqLiteHandler)
        setCurrentFragment(vetrinaFragment)

        val loginActivityLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
            if(result.resultCode == RESULT_OK){
                Toast.makeText(baseContext, "Login ok", Toast.LENGTH_LONG).show()
            }
        }

        floatingActionButton = findViewById<FloatingActionButton>(R.id.floating_button)
        val popupMenu = PopupMenu(baseContext, floatingActionButton)
        popupMenu.menuInflater.inflate(R.menu.popup_menu, popupMenu.menu)
        val menuItemCategory = popupMenu.menu.findItem(R.id.fam_item_search_cat)
        val menuItemVetrina = popupMenu.menu.findItem(R.id.fam_item_vetrina)
        val menuItemReports = popupMenu.menu.findItem(R.id.fam_reports)
        val menuItemLogin = popupMenu.menu.findItem(R.id.fam_login)
        floatingActionButton.setOnClickListener{
            popupMenu.setOnMenuItemClickListener {
                    menuItem ->
                // Ricerca per categoria
                if(menuItem.equals(menuItemCategory)){
                    setCurrentFragment(beerFragment)
                }else if(menuItem.equals(menuItemVetrina)){//move to vetrina fragment
                    setCurrentFragment(vetrinaFragment)
                }else if(menuItem.equals(menuItemReports)){
                    setCurrentFragment(reportsFragment)
                }else if(menuItem.equals(menuItemLogin)){
                    val intent = Intent(baseContext, LoginActivity::class.java)
                    loginActivityLauncher.launch(intent)
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