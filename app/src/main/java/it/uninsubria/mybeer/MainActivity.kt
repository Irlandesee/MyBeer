package it.uninsubria.mybeer

import android.content.ContentValues.TAG
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.navigationrail.NavigationRailView
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.google.firebase.database.getValue
import it.uninsubria.mybeer.fragments.BeerFragment

class MainActivity : AppCompatActivity() {

    val DATABASE_NAME = "https://mybeer-f68c5-default-rtdb.europe-west1.firebasedatabase.app"
    private lateinit var db: FirebaseDatabase
    private lateinit var dbRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        val beerFragment = BeerFragment()
        supportFragmentManager.beginTransaction().apply{
            replace(R.id.fragmentView, beerFragment)
            commit()
        }
        db = FirebaseDatabase.getInstance(DATABASE_NAME)
        dbRef = db.getReference("Altbier-Sticke")
        getData()

    }

    private fun getData(){
        dbRef.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(dataSnapshot: DataSnapshot){
                val value = dataSnapshot.getValue<Map<String, String>>()
                println(value)
            }

            override fun onCancelled(error: DatabaseError){
                Log.w(TAG, "FAILED TO READ VALUE", error.toException())
            }

        })

    }
}