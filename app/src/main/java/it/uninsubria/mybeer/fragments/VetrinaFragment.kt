package it.uninsubria.mybeer.fragments

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.PopupMenu
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.FirebaseDatabase
import it.uninsubria.mybeer.R
import it.uninsubria.mybeer.activities.StarRatingActivity
import it.uninsubria.mybeer.adapters.BeerListAdapter
import it.uninsubria.mybeer.datamodel.Beer
import it.uninsubria.mybeer.datamodel.Rating
import it.uninsubria.mybeer.dbHandler.DatabaseHandler
import it.uninsubria.mybeer.listeners.BeerClickListener
import it.uninsubria.mybeer.datamodel.User

class VetrinaFragment(
    private val db: FirebaseDatabase,
    private val handler: DatabaseHandler,
) : Fragment(), PopupMenu.OnMenuItemClickListener{
    private lateinit var beerListAdapter: BeerListAdapter
    private lateinit var recyclerView: RecyclerView
    private var beers: ArrayList<Beer?> = ArrayList()
    private lateinit var spinnerView: Spinner
    private lateinit var sqLiteDatabase: DatabaseHandler
    private lateinit var selectedBeer: Beer
    private lateinit var user: User
    private lateinit var viewReportLauncher: ActivityResultLauncher<Intent>
    private lateinit var starRatingLauncher: ActivityResultLauncher<Intent>

    private val defaultBeerStyle: String = "Seleziona una categoria"


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

        spinnerView = view.findViewById(R.id.spinnerView)
        val favBeerCategories: ArrayList<String?> = ArrayList()
        favBeerCategories.add(defaultBeerStyle)
        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(requireContext(), android.R.layout.simple_list_item_1)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        if(beers.size > 0){
            beers.forEach{ b -> if (b != null) { favBeerCategories.add(b.beer_style) } }
            adapter.addAll(favBeerCategories.distinct())
            spinnerView.adapter = adapter
            spinnerView.setSelection(0)
        }
        else{
            adapter.addAll(favBeerCategories)
            spinnerView.adapter = adapter
            spinnerView.setSelection(0)
        }


        starRatingLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
            if(result.resultCode == RESULT_OK && result.data != null){
                Toast.makeText(context, "Rating aggiunto", Toast.LENGTH_LONG).show()
                val rating = result.data!!.getSerializableExtra("it.uninsubria.mybeer.rating") as Rating
                sqLiteDatabase.addRating(selectedBeer, rating)
            }
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        spinnerView.onItemSelectedListener = object:
            AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long){
                //Toast.makeText(context, "${favBeerCategories[position]}", Toast.LENGTH_LONG).show()
                beers = sqLiteDatabase.getFavBeers(user)
                when(spinnerView.selectedItem){
                    defaultBeerStyle -> {
                        beerListAdapter.submitList(beers)
                    }
                    else -> {
                        beerListAdapter.submitList(ArrayList(
                            beers.filter{beer: Beer? ->
                                beer?.beer_style?.equals(spinnerView.selectedItem) == true
                            }))
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                return
            }

        }
    }

    override fun onStart(){
        super.onStart()
        user = sqLiteDatabase.getUser()
        val newBeers = sqLiteDatabase.getFavBeers(user)
        //Log.w(TAG, newBeers.toString())
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
    class VetrinaFragment(
        private val db: FirebaseDatabase,
        private val handler: DatabaseHandler,
    ) : Fragment(), PopupMenu.OnMenuItemClickListener{
        private lateinit var beerListAdapter: BeerListAdapter
        private lateinit var recyclerView: RecyclerView
        private var beers: ArrayList<Beer?> = ArrayList()
        private lateinit var spinnerView: Spinner
        private lateinit var sqLiteDatabase: DatabaseHandler
        private lateinit var selectedBeer: Beer
        private lateinit var user: User
        private lateinit var viewReportLauncher: ActivityResultLauncher<Intent>
        private lateinit var starRatingLauncher: ActivityResultLauncher<Intent>

        private val defaultBeerStyle: String = "Seleziona una categoria"

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

            spinnerView = view.findViewById(R.id.spinnerView)
            val favBeerCategories: ArrayList<String?> = ArrayList()
            favBeerCategories.add(defaultBeerStyle)
            val adapter: ArrayAdapter<String> = ArrayAdapter<String>(requireContext(), android.R.layout.simple_list_item_1)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            if(beers.size > 0){
                beers.forEach{ b -> if (b != null) { favBeerCategories.add(b.beer_style) } }
                adapter.addAll(favBeerCategories.distinct())
                spinnerView.adapter = adapter
                spinnerView.setSelection(0)
            }
            else{
                adapter.addAll(favBeerCategories)
                spinnerView.adapter = adapter
                spinnerView.setSelection(0)
            }


            starRatingLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
                if(result.resultCode == RESULT_OK && result.data != null){
                    Toast.makeText(context, "Rating aggiunto", Toast.LENGTH_LONG).show()
                    val rating = result.data!!.getSerializableExtra("it.uninsubria.mybeer.rating") as Rating
                    sqLiteDatabase.addRating(selectedBeer, rating)
                }
            }

            return view
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)
            spinnerView.onItemSelectedListener = object:
                AdapterView.OnItemSelectedListener{
                override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long){
                    //Toast.makeText(context, "${favBeerCategories[position]}", Toast.LENGTH_LONG).show()
                    beers = sqLiteDatabase.getFavBeers(user)
                    when(spinnerView.selectedItem){
                        defaultBeerStyle -> {
                            beerListAdapter.submitList(beers)
                        }
                        else -> {
                            beerListAdapter.submitList(ArrayList(
                                beers.filter{beer: Beer? ->
                                    beer?.beer_style?.equals(spinnerView.selectedItem) == true
                                }))
                        }
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    return
                }

            }
        }

        override fun onStart(){
            super.onStart()
            user = sqLiteDatabase.getUser()
            val newBeers = sqLiteDatabase.getFavBeers(user)
            //Log.w(TAG, newBeers.toString())
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
                R.id.beer_menu_add_rating ->{
                    Toast.makeText(requireContext(), "Aggiunta rating", Toast.LENGTH_LONG).show()
                    val intent = Intent(requireContext(), StarRatingActivity::class.java)
                    intent.putExtra("selected_beer", selectedBeer)
                    starRatingLauncher.launch(intent)
                }

            }
            return true
        }
    }
    override fun onMenuItemClick(item: MenuItem?): Boolean{
        when(item?.itemId){
            R.id.beer_menu_rm_from_fav -> {
                Toast.makeText(requireContext(), "Birra rimossa dalle preferite", Toast.LENGTH_LONG).show()
                sqLiteDatabase.rmFavBeer(selectedBeer, user)
                beerListAdapter.submitList(sqLiteDatabase.getFavBeers(user))
            }
            R.id.beer_menu_add_rating ->{
                Toast.makeText(requireContext(), "Aggiunta rating", Toast.LENGTH_LONG).show()
                val intent = Intent(requireContext(), StarRatingActivity::class.java)
                intent.putExtra("selected_beer", selectedBeer)
                starRatingLauncher.launch(intent)
            }

        }
        return true
    }
}