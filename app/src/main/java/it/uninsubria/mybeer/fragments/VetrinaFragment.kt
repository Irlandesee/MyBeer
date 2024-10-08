package it.uninsubria.mybeer.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import it.uninsubria.mybeer.R
import it.uninsubria.mybeer.listeners.BeerClickListener

class VetrinaFragment() : Fragment(), PopupMenu.OnMenuItemClickListener{

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.vetrina_fragment, container, false)
        ViewCompat.setOnApplyWindowInsetsListener(view.findViewById(R.id.vetrina_fragment)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, 0, systemBars.right, 0)
            insets
        }


        return view
    }

    private val beerClickListener = object: BeerClickListener {

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