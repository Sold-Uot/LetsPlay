package ru.radixit.letsplay.data.model

import android.annotation.SuppressLint
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

data class Error(
    @SerializedName("key")
    val key: String,

    @SerializedName("message")
    val message: String
)

@Parcelize
data class CreatedBy(
    val id: Int,
    val name: String,
    val surname: String?,
    val photo: Photo?
):Parcelable

@Parcelize
data class Preview(
    val id: Int,
    val url: String
):Parcelable

data class Gender(
    val label: String,
    val value: String
)

@Parcelize
data class Photo(
    val id: Int,
    val url: String
):Parcelable

data class AvatarResponse(
    val message: String,
    val objectURL: String,
    val status: String
)

data class ErrorServer(
    val key: String,
    val message: String
)

data class Option(
    val position: List<Position>
)

data class Position(
    val text: String,
    val value: Int
)

data class FieldSize(
    val length: Int,
    val width: Int
)

data class Geocode(
    val formattedAddress: String?,
    val lat: String?,
    val lng: String?
)

data class Schedule(


    val step: Int = 30,
    val timeEnd: String?,
    val timeStart: String?,
    val weekDay: Int

)

data class Member(
    val userId: Int
)

data class UploadPhoto(
    val base64File: String
)