package it.uninsubria.mybeer.activities

import android.content.ContentValues.TAG
import android.content.Intent
import android.content.SharedPreferences
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
import it.uninsubria.mybeer.fragments.RatingsFragment
import it.uninsubria.mybeer.fragments.VetrinaFragment

class BeerActivity : AppCompatActivity(){
    private val DATABASE_NAME = "https://mybeer-f68c5-default-rtdb.europe-west1.firebasedatabase.app"
    private lateinit var db: FirebaseDatabase
    private lateinit var floatingActionButton: FloatingActionButton
    private lateinit var sqLiteHandler: DatabaseHandler

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)

        //Check if user is still logged in
        val preferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val userName = preferences.getString("username", null)
        if(userName == null && !isSessionActive(preferences)){
            val loginActivityLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
                if(result.resultCode == RESULT_OK){
                    Log.d(TAG, "Login ok")
                }
            }
            val intent = Intent(this, LoginActivity::class.java)
            loginActivityLauncher.launch(intent)
        }

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
        val ratingsFragment = RatingsFragment(db, sqLiteHandler)
        setCurrentFragment(vetrinaFragment)


        floatingActionButton = findViewById<FloatingActionButton>(R.id.floating_button)
        val popupMenu = PopupMenu(baseContext, floatingActionButton)
        popupMenu.menuInflater.inflate(R.menu.popup_menu, popupMenu.menu)
        val menuItemCategory = popupMenu.menu.findItem(R.id.fam_item_search_cat)
        val menuItemVetrina = popupMenu.menu.findItem(R.id.fam_item_vetrina)
        val menuItemRatingFragment = popupMenu.menu.findItem(R.id.fam_beer_rate)
        floatingActionButton.setOnClickListener{
            popupMenu.setOnMenuItemClickListener {
                    menuItem ->
                if(menuItem.equals(menuItemCategory)){
                    setCurrentFragment(beerFragment)
                }else if(menuItem.equals(menuItemVetrina)){
                    setCurrentFragment(vetrinaFragment)
                }else if(menuItem.equals(menuItemRatingFragment)){
                    setCurrentFragment(ratingsFragment)
                }
                true
            }
            popupMenu.show()
        }
    }

    private fun isSessionActive(preferences: SharedPreferences): Boolean{
        val loginTime = preferences.getLong("login_time", -1)
        //Defining session duration: 30 minutes
        val sessionDuration = 30 * 60 * 1000
        return (System.currentTimeMillis() - loginTime) <= sessionDuration
    }

    private fun setCurrentFragment(fragment: Fragment) = supportFragmentManager.beginTransaction().apply{
        replace(R.id.nav_host_fragment, fragment)
        commit()
    }
}