package it.uninsubria.mybeer.datamodel

import com.google.android.gms.maps.model.LatLng

data class Places(
    val name: String,
    val latLng: LatLng,
    val address: LatLng,
    val rating: Float
)
