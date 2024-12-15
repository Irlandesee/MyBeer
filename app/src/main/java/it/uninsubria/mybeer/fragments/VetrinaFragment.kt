package it.uninsubria.mybeer.fragments

import android.app.Activity.RESULT_OK
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Build
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
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import it.uninsubria.mybeer.R
import it.uninsubria.mybeer.activities.ReportBeerActivity
import it.uninsubria.mybeer.adapters.BeerListAdapter
import it.uninsubria.mybeer.adapters.ReportListAdapter
import it.uninsubria.mybeer.datamodel.Beer
import it.uninsubria.mybeer.datamodel.Report
import it.uninsubria.mybeer.dbHandler.DatabaseHandler
import it.uninsubria.mybeer.listeners.BeerClickListener
import it.uninsubria.mybeer.datamodel.User
import it.uninsubria.mybeer.listeners.ReportClickListener

class VetrinaFragment(
    private val db: FirebaseDatabase,
    private val handler: DatabaseHandler,
) : Fragment(), PopupMenu.OnMenuItemClickListener{
    private lateinit var beerListAdapter: BeerListAdapter
    private lateinit var reportListAdapter: ReportListAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerReportView: RecyclerView
    private var beers: ArrayList<Beer?> = ArrayList()
    private var reports: ArrayList<Report> = ArrayList()
    private lateinit var autoCompleteView: AutoCompleteTextView
    private lateinit var sqLiteDatabase: DatabaseHandler
    private lateinit var dbRef: DatabaseReference
    private lateinit var selectedBeer: Beer
    private lateinit var selectedReport: Report
    private lateinit var user: User
    private lateinit var viewReportLauncher: ActivityResultLauncher<Intent>
    private lateinit var reportBeerLauncher: ActivityResultLauncher<Intent>
    private lateinit var floatingActionButton: FloatingActionButton


    @RequiresApi(Build.VERSION_CODES.O)
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
        reports = sqLiteDatabase.getReports()
        beerListAdapter = BeerListAdapter(beers, beerClickListener)


        recyclerView = view.findViewById(R.id.recycler_vetrina)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = beerListAdapter

        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(requireContext(), android.R.layout.simple_list_item_1)
        val favBeerCategories: ArrayList<String?> = ArrayList()
        beers.forEach{ b -> if (b != null) { favBeerCategories.add(b.beer_style) } }

        adapter.addAll(favBeerCategories.distinct())

        reportBeerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
            if(result.resultCode != RESULT_OK && result.data != null){
                val beerReport = result.data!!.getSerializableExtra("it.uninsubria.mybeer.report") as Report
                sqLiteDatabase.addReport(beerReport)
            }

        }

        viewReportLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            result ->
            if(result.resultCode == RESULT_OK)
                Toast.makeText(context, "ViewReportOk", Toast.LENGTH_LONG).show()
            else Toast.makeText(context, "ViewReportKo", Toast.LENGTH_LONG).show()
        }

        return view
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStart(){
        super.onStart()
        user = sqLiteDatabase.getUser()
        val newBeers = sqLiteDatabase.getFavBeers(user)
        val newReports = sqLiteDatabase.getReports()

        //Log.w(TAG, newBeers.toString())
        beerListAdapter.submitList(newBeers)
        //reportListAdapter.submitList(newReports)
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
            R.id.beer_menu_create_report-> {
                Toast.makeText(requireContext(), "Creating report", Toast.LENGTH_LONG).show()
                val intent = Intent(context, ReportBeerActivity::class.java)
                intent.putExtra("selected_beer", selectedBeer)
                reportBeerLauncher.launch(intent)
            }

        }
        return true
    }
}