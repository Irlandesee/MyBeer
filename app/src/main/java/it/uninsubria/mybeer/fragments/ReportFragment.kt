package it.uninsubria.mybeer.fragments
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
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import it.uninsubria.mybeer.R
import it.uninsubria.mybeer.activities.ViewReportActivity
import it.uninsubria.mybeer.adapters.ReportListAdapter
import it.uninsubria.mybeer.datamodel.Report
import it.uninsubria.mybeer.datamodel.User
import it.uninsubria.mybeer.dbHandler.DatabaseHandler
import it.uninsubria.mybeer.listeners.ReportClickListener

class ReportFragment (
    private val db: FirebaseDatabase,
    private val handler: DatabaseHandler
): Fragment(), PopupMenu.OnMenuItemClickListener{
    private lateinit var recyclerView: RecyclerView
    private lateinit var reportListAdapter: ReportListAdapter
    private var reports: ArrayList<Report?> = ArrayList()
    private lateinit var autoCompleteView: AutoCompleteTextView
    private lateinit var user: User
    private lateinit var selectedReport: Report
    private lateinit var sqLiteHandler: DatabaseHandler
    private lateinit var dbRef: DatabaseReference
    private lateinit var viewReportLauncher: ActivityResultLauncher<Intent>


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedIntanceState: Bundle?) : View?{
        val view = inflater.inflate(R.layout.report_fragment, container, false)
        ViewCompat.setOnApplyWindowInsetsListener(view.findViewById(R.id.fragment_report)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, 0, systemBars.right, 0)
            insets
        }

        sqLiteHandler = handler
        user = sqLiteHandler.getUser()

        recyclerView = view.findViewById(R.id.recycler_home)
        recyclerView.layoutManager = LinearLayoutManager(context)

        autoCompleteView = view.findViewById(R.id.autoCompleteView)
        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(requireContext(), android.R.layout.simple_list_item_1)
        val reportList: ArrayList<String?> = ArrayList()
        reports.forEach{ r -> if (r != null){reportList.add(r.beer_name + "@" + r.date)} }
        adapter.addAll(reportList)

        autoCompleteView.onItemClickListener = AdapterView.OnItemClickListener{
                parent, _, position, _ ->
            val item = parent.getItemAtPosition(position).toString()
            //TODO: filter reports
        }

        reportListAdapter = ReportListAdapter(reports, reportClickListener)
        recyclerView.adapter = reportListAdapter

        viewReportLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
            if(result.resultCode == RESULT_OK){
                Toast.makeText(context, "ViewReportOk", Toast.LENGTH_LONG).show()
            }else{ Toast.makeText(context, "ViewReportKo", Toast.LENGTH_LONG).show() }
        }

        return view
    }

    override fun onStart(){
        super.onStart()
        user = sqLiteHandler.getUser()
    }

    private fun createPopupReportMenu(cardView: CardView){
        val popupMenu = PopupMenu(requireContext(), cardView)
        popupMenu.setOnMenuItemClickListener(this)
        popupMenu.inflate(R.menu.report_beer_menu)
        popupMenu.show()
    }

    private val reportClickListener = object: ReportClickListener {
        override fun onLongClick(index: Int, cardView: CardView) {
            selectedReport = reportListAdapter.getList()[index]!!
            val intent = Intent(context, ViewReportActivity::class.java)
            Log.w(TAG, "Vetrina fragment: $selectedReport")
            intent.putExtra("it.uninsubria.mybeer.report", selectedReport)
            viewReportLauncher.launch(intent)
        }
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean{

        return true
    }


}