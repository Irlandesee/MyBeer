package it.uninsubria.mybeer.activities

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import it.uninsubria.mybeer.R

class ViewReportActivity : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_view_report)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)){ v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val tvReportBeerName: TextView = findViewById(R.id.tv_report_beer_name)
        val tvReportBeerStyle: TextView = findViewById(R.id.tv_report_beer_style)
        val tvReportBeerBrewery: TextView = findViewById(R.id.tv_report_beer_brewery)
        val tvReportBeerNotes: TextView = findViewById(R.id.tv_report_notes)
        val ivReportBeerImage: ImageView = findViewById(R.id.iv_report_image)
        val floatingActionButton: FloatingActionButton = findViewById(R.id.floating_button)


        floatingActionButton.setOnClickListener{
            setResult(RESULT_OK)
            finish()
        }
    }
}