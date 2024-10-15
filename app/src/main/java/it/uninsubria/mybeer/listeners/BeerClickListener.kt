package it.uninsubria.mybeer.listeners

import androidx.cardview.widget.CardView

interface BeerClickListener {
    fun onLongClick(index: Int, cardView: CardView)
    fun onPictureClick(index: Int)
    fun onDeleteClick(index: Int)
}