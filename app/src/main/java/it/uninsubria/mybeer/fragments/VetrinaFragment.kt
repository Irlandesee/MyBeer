package it.uninsubria.mybeer.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.PopupMenu
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import it.uninsubria.mybeer.R
import it.uninsubria.mybeer.adapters.BeerListAdapter
import it.uninsubria.mybeer.datamodel.Beer
import it.uninsubria.mybeer.dbHandler.DatabaseHandler
import it.uninsubria.mybeer.listeners.BeerClickListener
import it.uninsubria.mybeer.datamodel.User

class VetrinaFragment(
    private val db: FirebaseDatabase,
    private val handler: DatabaseHandler
) : Fragment(), PopupMenu.OnMenuItemClickListener{
    private lateinit var recyclerView: RecyclerView
    private lateinit var beerListAdapter: BeerListAdapter
    private var beers: ArrayList<Beer?> = ArrayList()
    private lateinit var autoCompleteView: AutoCompleteTextView
    private lateinit var sqLiteDatabase: DatabaseHandler
    private lateinit var dbRef: DatabaseReference
    private lateinit var selectedBeer: Beer
    private lateinit var user: User


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.vetrina_fragment, container, false)
        ViewCompat.setOnApplyWindowInsetsListener(view.findViewById(R.id.vetrina_fragment)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, 0, systemBars.right, 0)
            insets
        }
        this.sqLiteDatabase = handler
        this.user = this.sqLiteDatabase.getUser()

        recyclerView = view.findViewById(R.id.recycler_vetrina)
        recyclerView.layoutManager = LinearLayoutManager(context)
        beerListAdapter = BeerListAdapter(handler.getFavBeers(user))
        recyclerView.adapter = beerListAdapter

        autoCompleteView = view.findViewById(R.id.autoCompleteView)
        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(requireContext(), android.R.layout.simple_list_item_1)
        val favBeerCategories: ArrayList<String> = ArrayList()
        beers.forEach{ b ->
            if (b != null) {
                b.beer_style?.let { favBeerCategories.add(it) }
            }
        }
        adapter.addAll(favBeerCategories)
        autoCompleteView.onItemClickListener = AdapterView.OnItemClickListener{
            parent, view, position, it ->
                val item = parent.getItemAtPosition(position).toString()
                Toast.makeText(requireContext(), "Item Clicked $item", Toast.LENGTH_LONG).show()
        }


        return view
    }

    private val beerClickListener = object: BeerClickListener {

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