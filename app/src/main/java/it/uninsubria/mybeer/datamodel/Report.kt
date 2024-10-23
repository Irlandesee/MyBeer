package it.uninsubria.mybeer.datamodel

import java.io.Serializable

data class Report (
    var beer_name: String? = "",
    var beer_style: String? = "",
    var beer_brewery: String? = "",
    var notes: String? = "",
    var beer_picture_link: String? = ""
): Serializable{
    override fun toString(): String {
       return "Report[$beer_name, $beer_style, $beer_brewery, $notes, $beer_picture_link]"
    }

}
