package it.uninsubria.mybeer.datamodel

import it.uninsubria.mybeer.datamodel.Beer

data class User (var name: String? = "",
                 var surname: String? = "",
                 var id: String? = "",
                 var password: String? = "",
                 var favBeers: ArrayList<Pair<String, String>> = ArrayList()){

    override fun toString(): String {
        return "User[$id]"
    }
}