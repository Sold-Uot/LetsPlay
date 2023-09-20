package ru.radixit.letsplay.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

data class Playground(
    val address: String,
    val coating: String,
    val id: Int,
    val photos: List<PhotoModel>,
    val price: String,
    val rating: Double,
    val ratingsCount: Int,
    val schedule: String,
    val title: String,
    val verified: Boolean
)

data class PhotoModel(
    val id: Int,
    val url: String
)


@Parcelize
data class PlaygroundInDetail(
    val general: General,
    val more: More,
    val status: String,
    val title: String
) : Parcelable {
    @Parcelize
    data class General(
    val address: String,
    val coating: Int,
    val phone: String,
    val price: String,
    val rating: String?,
    val ratingsCount: Int,
    val schedule: List<Schedule>
    ) : Parcelable

    @Parcelize
    data class More(
        val ball: String?,
        val changingRooms: String?,
        val fieldSize: String?,
        val lighting: String?,
        val manishki: String?,
        val paymentByTransfer: String?,
        val shower: String?,
        val studdedCleats: String?
    ) : Parcelable

    @Parcelize
    data class Photo(
        val id: Int,
        val url: String? = null
    ) : Parcelable

    @Parcelize
    data class Schedule(
        val time: String,
        val weekDay: String
    ) : Parcelable
}

data class PlaygroundForMap(
    val address: String,
    val freeHours: String,
    val id: Int,
    val photos: List<PhotoModel>,
    val price: String,
    val rating: String,
    val ratingsCount: Int,
    val status: String,
    val title: String
)

data class Calendar(
    val disabled: Boolean,
    val key: Int,
    val value: String
)