package it.uninsubria.mybeer.datamodel

import java.io.Serializable
import java.time.LocalDate

data class Report (
    var report_id: String = "",
    var beer_name: String? = "",
    var beer_style: String? = "",
    var beer_brewery: String? = "",
    var notes: String? = "",
    var report_picture_link: String? = "",
    var date: LocalDate
): Serializable{
    override fun toString(): String {
       return "Report[$report_id, $beer_name, $beer_style, $beer_brewery, $notes, $report_picture_link]"
    }

}
