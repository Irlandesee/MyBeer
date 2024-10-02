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

class BeerListAdapter(private var beerList: ArrayList<Beer?>):
    RecyclerView.Adapter<BeerViewHolder>(){

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BeerViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.beer_list, parent, false)
            return BeerViewHolder(view)
        }
        override fun getItemCount(): Int = beerList.size

        override fun onBindViewHolder(holder: BeerViewHolder, position: Int){
            holder.tvBeerTitle.text = beerList[position]?.beer_name
            holder.tvBeerStyle.text = beerList[position]?.beer_style
            holder.tvBeerBrewery.text = beerList[position]?.beer_brewery
            holder.tvBeerAbv.text = beerList[position]?.beer_abv
            holder.tvBeerDesc.text = beerList[position]?.beer_desc
        }

}

class BeerViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
    private val beerContainer: CardView = itemView.findViewById(R.id.beer_container)
    val tvBeerTitle: TextView = itemView.findViewById(R.id.tvBeerName)
    val tvBeerStyle: TextView = itemView.findViewById(R.id.tvBeerStyle)
    val tvBeerBrewery: TextView = itemView.findViewById(R.id.tvBeerBrewery)
    val tvBeerAbv: TextView = itemView.findViewById(R.id.tvBeerAbv)
    val tvBeerDesc: TextView = itemView.findViewById(R.id.tvBeerDesc)
    val ivPicture: ImageView = itemView.findViewById(R.id.iv_picture)

    init{
        itemView.setOnClickListener{
            //listener.onClick()
        }
        itemView.setOnLongClickListener{
            //listener.onLongClick(bindingAdapterPosition, beerContainer)
            true
        }
        //ivPicture.setOnClickListener{ listener.onPictureClick(bindingAdapterPosition)}

    }

}