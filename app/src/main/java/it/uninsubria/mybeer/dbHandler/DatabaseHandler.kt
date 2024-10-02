package it.uninsubria.mybeer.dbHandler

import android.content.ContentValues.TAG
import android.util.Log
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.getValue
import it.uninsubria.mybeer.datamodel.Beer

class DatabaseHandler(db: FirebaseDatabase){
    private val databaseName: String = "https://mybeer-f68c5-default-rtdb.europe-west1.firebasedatabase.app"
    private lateinit var db: FirebaseDatabase
    private lateinit var dbRef: DatabaseReference

    val childEventListener = object: ChildEventListener {

        override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?){
            Log.d(TAG, "OnChildAdded: " + dataSnapshot.key!!)
            //A new beer has been added, add it to the displayed list
            val beer = dataSnapshot.getValue<Beer>()
            println(beer)
        }

        override fun onChildChanged(dataSnapshot: DataSnapshot, previousChildName: String?){
            Log.d(TAG, "onChildChanged: $dataSnapshot.key")
            //A beer has changed, use the key to determine if we are displaying this beer
            //and if so display the changed beer

            val newBeer = dataSnapshot.getValue<Beer>()
            val beerKey = dataSnapshot.key
        }

        override fun onChildRemoved(dataSnapshot: DataSnapshot){
            Log.d(TAG, "onChildRemoved:" + dataSnapshot.key!!)
            //A beer has changed, use the key to determine if we are displaying this beer and
            //if so remove it
            val commentKey = dataSnapshot.key
        }

        override fun onChildMoved(dataSnapshot: DataSnapshot, previousChildName: String?){
            Log.d(TAG, "onChildMoved:" + dataSnapshot.key!!)
            //A beer has changed position, use the key to determine if we are displaying this
            //beer and if so move it
            val movedBeer = dataSnapshot.getValue<Beer>()
            val beerKey = dataSnapshot.key
        }

        override fun onCancelled(dbError: DatabaseError){
            Log.w(TAG, "onCancelled", dbError.toException())
            println("Failed to load beers")
        }

    }

}