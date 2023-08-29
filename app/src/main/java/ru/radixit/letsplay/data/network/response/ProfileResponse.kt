package ru.radixit.letsplay.data.network.response

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import ru.radixit.letsplay.data.model.*

data class MeResponse(
    val status: String,
    val id: Int,
    val name: String,
    val surname: String,
    val notif: String,
    val email: String
)

data class EventResponse(
    val list: List<Event>,
    val pages: Int,
    val status: String,
    val total: Int
)

@Parcelize
data class Playground(
    val id: Int,
    val title: String
):Parcelable

data class ProfileResponse(
    val address: String? = null,
    val age: Int? = null,
    val birthday: String? = null,
    val email: Any? = null,
    val friendsRandom: List<User>,
    val friends: List<User>,
    val gender: Gender? = null,
    val height: Int? = null,
    val id: Int,
    val stateFriend: Int? = null,
    val matchesPlayed: Int? = null,
    val name: String? = null,
    val photo: Photo? = null,
    val position: String? = null,
    val status: String? = null,
    val surname: String? = null,
    val userType: String? = null,
    val wasNotPresentGame: Int? = null,
    val weight: Int? = null
)

data class ProfileBlackListResponse(
    val list: List<User>,
    val pages: Int,
    val status: String,
    val total: Int
)

data class EditProfileResponse(
    val message: String,
    val status: String,
    val errors: List<ErrorServer>
)

data class UnblockResponse(
    val status: String,
    val message: String
)

data class EmailResponse(
    val status: String,
    val message: String
)

data class UserInfoResponse(
    val address: String? = null,
    val birthday: String? = null,
    val email: String? = null,
    val gender: Gender? = null,
    val height: Int? = null,
    val id: Int? = null,
    val isBlock: Boolean? = null,
    val matchesPlayed: Int? = null,
    val name: String? = null,
    val photo: Photo? = null,
    val position: String? = null,
    val status: String? = null,
    val surname: String? = null,
    val user: User? = null,
    val username: String? = null,
    val wasNotPresentGame: Int? = null,
    val weight: String? = null,
    val stateFriend: Int? = null
)

data class FetchEditProfileResponse(
    val `data`: Data,
    val option: Option,
    val status: String
)

data class Data(
    val birthday: String? = null,
    val email: String? = null,
    val gender: Int? = null,
    val height: String? = null,
    val name: String? = null,
    val photo: Photo? = null,
    val position: Int? = null,
    val surname: String? = null,
    val weight: String? = null
)

data class NotificationsSettingsResponse(
    val noticeEvent: Boolean,
    val noticeFriend: Boolean,
    val noticeInvitation: Boolean,
    val noticeMessage: Boolean,
    val status: String? = ""
)