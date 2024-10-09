package it.uninsubria.mybeer.user

import it.uninsubria.mybeer.datamodel.Beer

data class User (var name: String? = "",
                 var surname: String? = "",
                 var userId: String? = "",
                 var password: String? = "",
                 var favBeers: ArrayList<Beer?> = ArrayList()){

    fun setUserId(userId: String){this.userId = userId}
    fun setPassword(password: String){this.password = password}
    fun setName(name: String){this.name = name}
    fun setSurname(surname: String) {this.surname = surname}
    fun setFavBeers(favBeers: ArrayList<Beer?>) {this.favBeers = favBeers}

    override fun toString(): String {
        return "User[$userId]"
    }
}