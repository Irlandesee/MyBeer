package it.uninsubria.mybeer.fragments

import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import it.uninsubria.mybeer.dbHandler.DatabaseHandler

class FavBeerFragment(
    private val handler: DatabaseHandler
) : Fragment(), PopupMenu.OnMenuItemClickListener{
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?{

    }
}