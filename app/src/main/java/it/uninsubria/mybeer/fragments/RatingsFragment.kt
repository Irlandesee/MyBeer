package it.uninsubria.mybeer.fragments
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
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
import it.uninsubria.mybeer.adapters.RatingsAdapter
import it.uninsubria.mybeer.datamodel.Rating
import it.uninsubria.mybeer.datamodel.User
import it.uninsubria.mybeer.dbHandler.DatabaseHandler
import it.uninsubria.mybeer.listeners.BeerClickListener

class RatingsFragment(
    private val db: FirebaseDatabase,
    private val handler: DatabaseHandler
) : Fragment(), PopupMenu.OnMenuItemClickListener{

    private lateinit var recyclerView: RecyclerView
    private lateinit var ratingsAdapter: RatingsAdapter
    private var dbHandler: DatabaseHandler = handler
    private lateinit var dbRef: DatabaseReference
    private lateinit var user: User
    private lateinit var selectedRating: Rating

    private var ratings : ArrayList<Rating> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_ratings, container, false)
        ViewCompat.setOnApplyWindowInsetsListener(view.findViewById(R.id.fragment_ratings)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, 0, systemBars.right, 0)
            insets
        }
        recyclerView = view.findViewById(R.id.recyclerView)
        user = dbHandler.getUser()
        ratings = dbHandler.getRatings()

        ratingsAdapter = RatingsAdapter(requireContext(), ratings, ratingClickListener)
        recyclerView.adapter = ratingsAdapter

        return view
    }

    override fun onStart(){
        super.onStart()
        user = dbHandler.getUser()
        val newRatings = dbHandler.getRatings()
        ratingsAdapter.submitList(newRatings)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = view.findViewById(R.id.recyclerView)

        loadRatings()
    }

    private fun loadRatings() {
        ratings = dbHandler.getRatings()
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        ratingsAdapter = RatingsAdapter(requireContext(), ratings, ratingClickListener)
        recyclerView.adapter = ratingsAdapter
    }

    private fun createRatingsPopupBeerMenu(cardView: CardView){
        val popupMenu = PopupMenu(requireContext(), cardView)
        popupMenu.setOnMenuItemClickListener(this)
        popupMenu.inflate(R.menu.ratings_beer_menu)
        popupMenu.show()
    }


    private val ratingClickListener = object: BeerClickListener {
        override fun onLongClick(index: Int, cardView: CardView){
            selectedRating = ratingsAdapter.getList()[index]
            createRatingsPopupBeerMenu(cardView)
        }
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean{
        when(item?.itemId){
            R.id.fam_remove_rating -> {
                Toast.makeText(requireContext(), "Voto rimosso", Toast.LENGTH_LONG).show()
                dbHandler.removeRating(selectedRating)
                ratingsAdapter.submitList(dbHandler.getRatings())
            }
            R.id.fam_take_photo -> {
                Toast.makeText(requireContext(), "Foto alla birra", Toast.LENGTH_LONG).show()

            }
        }
        return true
    }

}