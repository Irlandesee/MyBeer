package it.uninsubria.mybeer.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import it.uninsubria.mybeer.R
import it.uninsubria.mybeer.datamodel.Rating
import it.uninsubria.mybeer.listeners.BeerClickListener

class RatingsAdapter(private val context: Context,
                     private var ratingsList: ArrayList<Rating>,
    private val listener: BeerClickListener
) :
    RecyclerView.Adapter<RatingsAdapter.RatingsViewHolder>() {

    inner class RatingsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ratingContainer: CardView = itemView.findViewById(R.id.rating_container)
        val tvBeerName: TextView = itemView.findViewById(R.id.tvBeerName)
        val tvRating: TextView = itemView.findViewById(R.id.tvRating)
        val tvDrunkDate: TextView = itemView.findViewById(R.id.tvDrunkDate)
        val tvLocation: TextView = itemView.findViewById(R.id.tvLocation)
        val ivBeerPicture: ImageView = itemView.findViewById(R.id.iv_picture)
        val starViews: List<ImageView> = listOf(
            itemView.findViewById(R.id.star_1),
            itemView.findViewById(R.id.star_2),
            itemView.findViewById(R.id.star_3),
            itemView.findViewById(R.id.star_4),
            itemView.findViewById(R.id.star_5)
        )

        fun bind(rating: Rating) {
            tvBeerName.text = rating.beer?.beer_name ?: "Birra sconosciuta"
            tvRating.text = "Voto: ${rating.rating}"
            tvDrunkDate.text = "Bevuta il: ${rating.drunkDate}"
            tvLocation.text = "Luogo: ${rating.drunkLocation}"

            setStarRating(rating.rating ?: 0)

            val requestOptions = RequestOptions()
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)
            Glide.with(itemView)
                .applyDefaultRequestOptions(requestOptions)
                .load(rating.beer?.beer_picture_link)
                .into(ivBeerPicture)
            ivBeerPicture.setOnClickListener{ toggleBeerPicture(rating) }
            ivBeerPicture.tag = "default"
        }

        private fun setStarRating(rating: Int) {
            starViews.forEachIndexed { index, imageView ->
                imageView.setImageResource(
                    if (index < rating) R.drawable.ic_star // Filled star
                    else R.drawable.ic_star_border // Empty star
                )
            }

        }

        private fun toggleBeerPicture(rating: Rating){
            if(ivBeerPicture.tag == "default"){
                Glide.with(context)
                    .load(rating.beer?.beer_picture_link)
                    .into(ivBeerPicture)
                ivBeerPicture.tag = "user"
            }else{
                val bitmap = BitmapFactory.decodeFile(rating.photoUri)
                ivBeerPicture.setImageBitmap(bitmap)
                ivBeerPicture.tag = "default"
            }

        }

        init{
            itemView.setOnLongClickListener{
                listener.onLongClick(adapterPosition, ratingContainer)
                true
            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RatingsViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.rating_card_layout, parent, false)
        return RatingsViewHolder(view)
    }

    override fun onBindViewHolder(holder: RatingsViewHolder, position: Int) {
        holder.bind(ratingsList[position])
    }

    override fun getItemCount(): Int = ratingsList.size

    @SuppressLint("NotifyDataSetChanged")
    fun submitList(ratingsList: ArrayList<Rating>){
        this.ratingsList.clear()
        this.ratingsList = ratingsList
        notifyDataSetChanged()
    }

    fun getList(): ArrayList<Rating> {
        return this.ratingsList
    }

}