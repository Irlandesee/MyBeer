package it.uninsubria.mybeer.fragments

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.ContentValues.TAG
import android.content.Intent
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
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import it.uninsubria.mybeer.MainActivity
import it.uninsubria.mybeer.R
import it.uninsubria.mybeer.adapters.BeerListAdapter
import it.uninsubria.mybeer.datamodel.Beer
import it.uninsubria.mybeer.datamodel.Report
import it.uninsubria.mybeer.datamodel.User
import it.uninsubria.mybeer.dbHandler.DatabaseHandler
import it.uninsubria.mybeer.listeners.BeerClickListener
import java.io.File
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.text.Charsets.UTF_8

class BeerFragment(
    private val db: FirebaseDatabase,
    private val handler: DatabaseHandler,
    ): Fragment(), PopupMenu.OnMenuItemClickListener {
    private lateinit var recyclerView: RecyclerView
    private lateinit var beerListAdapter: BeerListAdapter
    private var beers: ArrayList<Beer?> = ArrayList()
    private lateinit var autoCompleteView: AutoCompleteTextView

    private lateinit var sqLiteHandler: DatabaseHandler
    private lateinit var dbRef: DatabaseReference
    private lateinit var selectedBeer: Beer
    private var beerCategories: HashMap<String, String> = HashMap<String, String>()
    private lateinit var user: User
    private lateinit var takePictureLauncher: ActivityResultLauncher<Intent>
    private lateinit var photoFile: File

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?{
        val view = inflater.inflate(R.layout.beer_fragment, container, false)

        ViewCompat.setOnApplyWindowInsetsListener(view.findViewById(R.id.beer_fragment)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, 0, systemBars.right, 0)
            insets
        }
        sqLiteHandler = handler
        user = sqLiteHandler.getUser()
        beerCategories = sqLiteHandler.getAllBeerCategories()

        recyclerView = view.findViewById(R.id.recycler_home)
        recyclerView.layoutManager = LinearLayoutManager(context)
        beerListAdapter = BeerListAdapter(beers, beerClickListener)
        recyclerView.adapter = beerListAdapter

        photoFile = createImageFile()

        takePictureLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()){
            result ->
            if(result.resultCode == RESULT_OK && result.data != null){
                val newReport = result.data!!.getSerializableExtra("it.uninsubria.mybeer.report") as Report
                sqLiteHandler.addReport(newReport)

            }

        }

        autoCompleteView = view.findViewById(R.id.autoCompleteView)
        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(requireContext(), android.R.layout.simple_list_item_1)
        adapter.addAll(beerCategories.values)
        autoCompleteView.setAdapter(adapter)
        autoCompleteView.onItemClickListener = AdapterView.OnItemClickListener{
                parent, _, position, _ ->
            val item = parent.getItemAtPosition(position).toString()
            val key = beerCategories.filterValues{ it == item }.keys.first()
            dbRef = db.getReference(key)
            dbRef.addValueEventListener(object: ValueEventListener{

                override fun onDataChange(snapshot: DataSnapshot) {
                    val l: ArrayList<Beer?> = ArrayList()
                    snapshot.children.forEach{ child ->
                        Log.w(TAG, "$child.value")
                        l.add(child.getValue(Beer::class.java))}
                    beerListAdapter.submitList(l)
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.w(TAG, "error while reading database", error.toException())
                }

            })
        }

        return view
    }

    override fun onStart(){
        super.onStart()
        user = sqLiteHandler.getUser()
    }

    private fun compareLengthThenString(a: String, b: String): Int = compareValuesBy(a, b, {it.length}, {it})

    private fun createPopupBeerMenu(cardView: CardView){
        val popupMenu = PopupMenu(requireContext(), cardView)
        popupMenu.setOnMenuItemClickListener(this)
        popupMenu.inflate(R.menu.beer_menu)
        popupMenu.show()
    }

    private val beerClickListener = object: BeerClickListener{

        override fun onLongClick(index: Int, cardView: CardView){
            selectedBeer = beerListAdapter.getList()[index]!!
            createPopupBeerMenu(cardView)
        }

    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onMenuItemClick(item: MenuItem?): Boolean{
        when(item?.itemId){
            R.id.beer_menu_add_to_fav -> {
                Toast.makeText(requireContext(), "Birra aggiunta alle preferite", Toast.LENGTH_LONG).show()
                sqLiteHandler.addFavBeer(selectedBeer, user)
            }
            R.id.beer_menu_create_report -> {
                Toast.makeText(requireContext(), "Crea rapporto birra", Toast.LENGTH_LONG).show()

            }
        }
        return true
    }

    private fun createImageFile(): File{
        val timeStamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss".format(Date()), Locale.getDefault())
        val imageFileName = "PNG_" + timeStamp + "_"
        return File.createTempFile(imageFileName, ".png", )
    }


}