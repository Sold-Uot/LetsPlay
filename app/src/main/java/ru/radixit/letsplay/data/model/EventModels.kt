package ru.radixit.letsplay.data.model

import android.annotation.SuppressLint
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import ru.radixit.letsplay.data.network.response.Playground
@SuppressLint("event")
@Parcelize
data class Event(
    val address: String? = null,
    val countMember: String? = null,
    val createdBy: CreatedBy? = null,
    val gameLevel: String? = null,
    val id: Int? = null,
    val my: Boolean? = null,
    val playground: Playground? = null,
    val preview: Preview? = null,
    val privacy: String? = null,
    val start: String? = null,
    val status: String? = null,
    val title: String? = null,
    val type: String? = null
):Parcelable