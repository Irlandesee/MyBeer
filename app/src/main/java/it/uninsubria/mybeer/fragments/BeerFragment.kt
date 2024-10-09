package it.uninsubria.mybeer.fragments

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.PopupMenu
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.fragment.app.FragmentActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.findNavController
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import it.uninsubria.mybeer.R
import it.uninsubria.mybeer.adapters.BeerListAdapter
import it.uninsubria.mybeer.datamodel.Beer
import it.uninsubria.mybeer.dbHandler.DatabaseHandler
import it.uninsubria.mybeer.listeners.BeerClickListener
import java.security.MessageDigest
import kotlin.text.Charsets.UTF_8

class BeerFragment(
    private val db: FirebaseDatabase,
    private val handler: DatabaseHandler
): Fragment(), PopupMenu.OnMenuItemClickListener {
    private lateinit var recyclerView: RecyclerView
    private lateinit var beerListAdapter: BeerListAdapter
    private var beers: ArrayList<Beer?> = ArrayList()
    private lateinit var autoCompleteView: AutoCompleteTextView

    private lateinit var sqLiteHandler: DatabaseHandler
    private lateinit var dbRef: DatabaseReference

    private lateinit var selectedBeer: Beer
    private var beerCategories: HashMap<String, String> = HashMap<String, String>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?{
        val view = inflater.inflate(R.layout.beer_fragment, container, false)

        ViewCompat.setOnApplyWindowInsetsListener(view.findViewById(R.id.beer_fragment)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, 0, systemBars.right, 0)
            insets
        }

        sqLiteHandler = handler
        beerCategories = sqLiteHandler.getAllBeerCategories()

        recyclerView = view.findViewById(R.id.recycler_home)
        recyclerView.layoutManager = LinearLayoutManager(context)
        beerListAdapter = BeerListAdapter(beers)
        recyclerView.adapter = beerListAdapter

        autoCompleteView = view.findViewById(R.id.autoCompleteView)
        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(requireContext(), android.R.layout.simple_list_item_1)
        adapter.addAll(beerCategories.values)
        autoCompleteView.setAdapter(adapter)
        autoCompleteView.onItemClickListener = AdapterView.OnItemClickListener{
            parent, view, position, it ->
                val item = parent.getItemAtPosition(position).toString()
                val key = beerCategories.filterValues{ it == item }.keys.first()
                dbRef = db.getReference(key)
                dbRef.addValueEventListener(object: ValueEventListener{

                    override fun onDataChange(snapshot: DataSnapshot) {
                        val l: ArrayList<Beer?> = ArrayList()
                        snapshot.children.forEachIndexed{_, child -> l.add(child.getValue(Beer::class.java))}
                        beerListAdapter.submitList(l)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.w(TAG, "error while reading database", error.toException())
                    }

                })
        }
        return view
    }

    private fun ByteArray.toHex() = joinToString(separator = ""){byte -> "%02x".format(byte)}
    private fun hashString(str: String): ByteArray = MessageDigest.getInstance("MD5").digest(str.toByteArray(UTF_8))
    private fun compareLengthThenString(a: String, b: String): Int = compareValuesBy(a, b, {it.length}, {it})

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