package ru.radixit.letsplay.data.network.response

import com.google.gson.annotations.SerializedName
import ru.radixit.letsplay.data.model.Suggestion


data class DaDataResponse(
    @SerializedName("suggestions")
    val suggestions: List<Suggestion>
)
