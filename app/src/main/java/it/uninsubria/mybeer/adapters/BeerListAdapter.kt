package it.uninsubria.mybeer.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import it.uninsubria.mybeer.R
import it.uninsubria.mybeer.datamodel.Beer
import it.uninsubria.mybeer.listeners.BeerClickListener

class BeerListAdapter (
    private val beerList: List<Beer>,
    private val listener: BeerClickListener
    ): RecyclerView.Adapter<BeerViewHolder>(){
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BeerViewHolder{
            return BeerViewHolder(
                listener,
                LayoutInflater.from(parent.context).inflate(R.layout.beer_list, parent, false)
            )
        }
        override fun getItemCount(): Int{ return beerList.size }

        override fun onBindViewHolder(holder: BeerViewHolder, position: Int){

        }
}

class BeerViewHolder(listener: BeerClickListener, itemView: View): RecyclerView.ViewHolder(itemView){
    private val beerContainer: CardView = itemView.findViewById(R.id.beer_container)
    val tvBeerTitle: TextView = itemView.findViewById(R.id.tvBeerName)
    val tvBeerStyle: TextView = itemView.findViewById(R.id.tvBeerStyle)
    val tvBeerBrewery: TextView = itemView.findViewById(R.id.tvBeerBrewery)
    val tvBeerAbv: TextView = itemView.findViewById(R.id.tvBeerAbv)
    val tvBeerDesc: TextView = itemView.findViewById(R.id.tvBeerDesc)
    val ivPicture: ImageView = itemView.findViewById(R.id.iv_picture)

    init{
        itemView.setOnClickListener{
            //listener.onClick(bindingAdapterPosition)
        }
        itemView.setOnLongClickListener{
            //listener.onLongClick(bindingAdapterPosition, beerContainer)
            true
        }
        //ivPicture.setOnClickListener{ listener.onPictureClick(bindingAdapterPosition)}

    }

}