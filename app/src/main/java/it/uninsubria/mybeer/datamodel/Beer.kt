package it.uninsubria.mybeer.datamodel

data class Beer (
    var beer_name: String? = "",
    var beer_style: String? = "",
    var beer_brewery: String? = "",
    var beer_abv: String? = "",
    var beer_ibu: String? = "",
    var beer_raters: String? = "",
    var beer_desc: String? = "",
    var beer_picture_link: String? = "",
    var beer_cat_hex: String? ="",
    var beer_name_hex: String? = ""
){
    override fun toString(): String{ return "Beer[name=$beer_name, style=$beer_style, pictureLink=$beer_picture_link]" }
}