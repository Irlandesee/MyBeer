package it.uninsubria.mybeer.listeners

import androidx.cardview.widget.CardView

interface BeerClickListener {
    fun onClick(index: Int)
    fun onLongClick(index: Int, cardView: CardView)
    fun onPictureClick(index: Int)
    fun onStarClick(index: Int)
    fun onDeleteClick(index: Int)
}