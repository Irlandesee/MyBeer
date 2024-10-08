package it.uninsubria.mybeer.user

data class User (var name: String? = "",
                 var surname: String? = "",
                 var userId: String? = "",
                 var password: String? = ""){

    override fun toString(): String {
        return "User[$userId]"
    }
}