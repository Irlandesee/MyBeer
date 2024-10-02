package it.uninsubria.mybeer.fragments

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import android.widget.SearchView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import it.uninsubria.mybeer.R
import it.uninsubria.mybeer.adapters.BeerListAdapter
import it.uninsubria.mybeer.datamodel.Beer
import it.uninsubria.mybeer.listeners.BeerClickListener

class BeerFragment(
    private var db: FirebaseDatabase
): Fragment(), PopupMenu.OnMenuItemClickListener {
    private lateinit var recyclerView: RecyclerView
    private lateinit var beerListAdapter: BeerListAdapter
    private lateinit var floatingActionButton: FloatingActionButton
    private var beers: ArrayList<Beer?> = ArrayList()
    private lateinit var searchView: SearchView
    private lateinit var selectedBeer: Beer

    private lateinit var dbRef: DatabaseReference

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?{
        val view = inflater.inflate(R.layout.beer_fragment, container, false)

        recyclerView = view.findViewById(R.id.recycler_home)
        recyclerView.layoutManager = LinearLayoutManager(context)
        beerListAdapter = BeerListAdapter(beers)
        recyclerView.adapter = beerListAdapter

        searchView = view.findViewById(R.id.search_view)
        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean{
                Log.w(TAG, "onQueryTextSubmit: $query")
                dbRef = db.getReference(query!!)

                dbRef.addValueEventListener(object: ValueEventListener{
                    override fun onDataChange(dataSnapshot: DataSnapshot){
                        val l: ArrayList<Beer?> = ArrayList()

                        for(child in dataSnapshot.children){
                            val b: Beer? = child.getValue(Beer::class.java)
                            l.add(b)
                        }

                        Log.w(TAG, "onDataChange: submitting list: ${l.size}")
                        beerListAdapter = BeerListAdapter(l)
                    }

                    override fun onCancelled(dbError: DatabaseError){
                        Log.w(TAG, "onCancelled: Error reading db, query: $query")
                    }

                })
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean{
                return false
            }

        })

        floatingActionButton = view.findViewById(R.id.add_beer_button)

        return view
    }


    private val beerClickListener = object: BeerClickListener{

        override fun onClick(index: Int){
            TODO("Not yet implemented")
        }

        override fun onLongClick(index: Int, cardView: CardView){
            TODO("Not yet implemented")
        }

        override fun onStarClick(index: Int) {
            TODO("Not yet implemented")
        }
        override fun onPictureClick(index: Int){
            TODO("Not yet implemented")
        }

        override fun onDeleteClick(index: Int){
            TODO("Not yet implemented")
        }
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean{
        TODO("Not yed implemented")
    }

}