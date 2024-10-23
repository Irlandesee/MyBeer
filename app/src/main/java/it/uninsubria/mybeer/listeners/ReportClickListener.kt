package it.uninsubria.mybeer.listeners

import androidx.cardview.widget.CardView

interface ReportClickListener {
    fun onLongClick(index: Int, cardView: CardView)
}