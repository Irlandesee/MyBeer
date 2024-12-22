package it.uninsubria.mybeer.datamodel

import java.io.Serializable

class Rating(
    val beer: Beer?,
    val ratingId: String?,
    val rating: Int?,
    val drunkDate: String?,
    val drunkLocation: String?
): Serializable {
    override fun toString(): String {
        return "Rating[$beer, $rating, $drunkDate, $drunkLocation]"
    }

}