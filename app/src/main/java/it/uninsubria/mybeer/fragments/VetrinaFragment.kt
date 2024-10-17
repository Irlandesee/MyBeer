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
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
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
    private val handler: DatabaseHandler,
) : Fragment(), PopupMenu.OnMenuItemClickListener{
    lateinit var beerListAdapter: BeerListAdapter
    private lateinit var recyclerView: RecyclerView
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
        user = sqLiteDatabase.getUser()
        beers = sqLiteDatabase.getFavBeers(user)
        beerListAdapter = BeerListAdapter(beers, beerClickListener)


        recyclerView = view.findViewById(R.id.recycler_vetrina)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = beerListAdapter

        autoCompleteView = view.findViewById(R.id.autoCompleteView)
        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(requireContext(), android.R.layout.simple_list_item_1)
        val favBeerCategories: ArrayList<String?> = ArrayList()
        beers.forEach{ b -> if (b != null) { favBeerCategories.add(b.beer_style) } }

        adapter.addAll(favBeerCategories.distinct())
        autoCompleteView.setAdapter(adapter)
        autoCompleteView.onItemClickListener = AdapterView.OnItemClickListener{
                parent, _, position, _ ->
            val item = parent.getItemAtPosition(position).toString()
            Toast.makeText(requireContext(), "Item Clicked $item", Toast.LENGTH_LONG).show()
        }

        return view
    }

    override fun onStart(){
        super.onStart()
        user = sqLiteDatabase.getUser()
        val newBeers = sqLiteDatabase.getFavBeers(user)
        Log.w(TAG, newBeers.toString())
        beerListAdapter.submitList(newBeers)
    }

    private fun createVetrinaPopupBeerMenu(cardView: CardView){
        val popupMenu = PopupMenu(requireContext(), cardView)
        popupMenu.setOnMenuItemClickListener(this)
        popupMenu.inflate(R.menu.vetrina_beer_menu)
        popupMenu.show()
    }

    private val beerClickListener = object: BeerClickListener {

        override fun onLongClick(index: Int, cardView: CardView){
            selectedBeer = beerListAdapter.getList()[index]!!
            createVetrinaPopupBeerMenu(cardView)
        }

    }

    override fun onMenuItemClick(item: MenuItem?): Boolean{
        when(item?.itemId){
            R.id.beer_menu_rm_from_fav -> {
                Toast.makeText(requireContext(), "Birra rimossa dalle preferite", Toast.LENGTH_LONG).show()
                sqLiteDatabase.rmFavBeer(selectedBeer, user)
                beerListAdapter.submitList(sqLiteDatabase.getFavBeers(user))
            }
            R.id.beer_menu_see_details -> {
                Toast.makeText(requireContext(), "Vedi dettagli birra", Toast.LENGTH_LONG).show()
                TODO("Not yed implemented")
            }

        }
        return true
    }
}