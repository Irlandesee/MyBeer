package it.uninsubria.mybeer.fragments

import android.os.Bundle
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
import com.google.android.material.search.SearchView
import it.uninsubria.mybeer.R
import it.uninsubria.mybeer.adapters.BeerListAdapter
import it.uninsubria.mybeer.datamodel.Beer
import it.uninsubria.mybeer.dbHandler.DatabaseHandler
import it.uninsubria.mybeer.listeners.BeerClickListener

class BeerFragment : Fragment(), PopupMenu.OnMenuItemClickListener {
    private lateinit var recyclerView: RecyclerView
    private lateinit var beerListAdapter: BeerListAdapter
    private lateinit var floatingActionButton: FloatingActionButton
    private var beers: MutableList<Beer> = mutableListOf()
    private lateinit var searchView: SearchView
    private lateinit var selectedBeer: Beer
    private lateinit var dbHandler: DatabaseHandler

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?{
        val view = inflater.inflate(R.layout.beer_fragment, container, false)
        dbHandler = DatabaseHandler(requireContext())
        recyclerView = view.findViewById(R.id.recycler_home)
        floatingActionButton = view.findViewById(R.id.add_beer_button)
        searchView = view.findViewById(R.id.search_view)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        beerListAdapter = BeerListAdapter(beers, beerClickListener)
        recyclerView.adapter = beerListAdapter


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