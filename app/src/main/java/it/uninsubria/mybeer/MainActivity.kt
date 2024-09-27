package it.uninsubria.mybeer

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.navigationrail.NavigationRailView
import it.uninsubria.mybeer.fragments.BeerFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        val beerFragment = BeerFragment()
        supportFragmentManager.beginTransaction().apply{
            replace(R.id.fragmentView, beerFragment)
            commit()
        }
    }
}