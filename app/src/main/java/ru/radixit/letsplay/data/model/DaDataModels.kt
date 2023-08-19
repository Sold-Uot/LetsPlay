package ru.radixit.letsplay.data.model

import com.google.gson.annotations.SerializedName

data class Suggestion(
    @SerializedName("data")
    val location: AddressLocation?,
    val value: String
)

data class AddressLocation(
    @SerializedName("geo_lat")
    val geoLat: String,
    @SerializedName("geo_lon")
    val geoLon: String
)