package it.uninsubria.mybeer.activities

import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import it.uninsubria.mybeer.R
import it.uninsubria.mybeer.datamodel.Report

class ViewReportActivity : AppCompatActivity(){
    private lateinit var selectedReport: Report
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_view_report)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)){ v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        selectedReport = intent.getSerializableExtra("it.uninsubria.mybeer.report") as Report
        val tvReportBeerName: TextView = findViewById(R.id.tv_report_beer_name)
        tvReportBeerName.text = selectedReport.beer_name
        val tvReportBeerStyle: TextView = findViewById(R.id.tv_report_beer_style)
        tvReportBeerStyle.text = selectedReport.beer_style
        val tvReportBeerBrewery: TextView = findViewById(R.id.tv_report_beer_brewery)
        tvReportBeerBrewery.text = selectedReport.beer_brewery
        val tvReportBeerNotes: TextView = findViewById(R.id.tv_report_notes)
        tvReportBeerNotes.text = selectedReport.notes
        val ivReportBeerImage: ImageView = findViewById(R.id.iv_report_image)
        ivReportBeerImage.setImageBitmap(BitmapFactory.decodeFile(selectedReport.beer_picture_link))
        val floatingActionButton: FloatingActionButton = findViewById(R.id.floating_button)

        floatingActionButton.setOnClickListener{
            setResult(RESULT_OK)
            finish()
        }
    }
}