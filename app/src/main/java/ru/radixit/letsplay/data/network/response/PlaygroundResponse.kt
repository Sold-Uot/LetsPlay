package ru.radixit.letsplay.data.network.response

import com.google.gson.annotations.SerializedName
import ru.radixit.letsplay.data.model.Calendar
import ru.radixit.letsplay.data.model.Error
import ru.radixit.letsplay.data.model.Playground
import ru.radixit.letsplay.data.model.PlaygroundInDetail

data class PlaygroundResponse(

    @SerializedName("list")
    val list: List<Playground>,

    @SerializedName("pages")
    val pages: Int,

    @SerializedName("status")
    val status: String,

    @SerializedName("total")
    val total: Int
)

data class MapsResponse(
    val status: String,
    val geocodes: List<Geocodes>
) {
    data class Geocodes(
        val id: Int,
        val lat: String,
        val lng: String
    )
}

data class CommentResponse(
    val list: List<Comment>,
    val pages: Int,
    val status: String,
    val total: Int
) {
    data class Comment(
        val createdBy: CreatedBy,
        val id: Int,
        val rating: Int,
        val text: String,
        val createdAt: CreatedAt,
    )

    data class CreatedBy(
        val id: Int,
        val name: String,
        val photo: PhotoComment?,
        val surname: String?
    )

    data class CreatedAt(
        val datetime: String,
        val timeSummary: String
    )
    data class PhotoComment(
        val id: Int,
        val url: String? = null
    )
}

data class ReportResponse(
    val message: String,
    val status: String
)

data class PhotoResponse(
    val message: String,
    val objectURL: String,
    val status: String
)

data class CreatePlaygroundResponse(
    val id: Int,
    val message: String,
    val status: String,
    val error: List<Error>
)

data class HoursFreeResponse(
    val `data`: String,
    val status: String
)

data class CreateChatResponse(
    val groupId: Int,
    val message: String,
    val status: String
)

data class UploadPhotoChatResponse(
    val status: String,
    val message: String,
    val errors: List<String>
)

data class PlaygroundsPhotosResponse(
    val status: String,
    val list: List<PlaygroundInDetail.Photo>
)

data class FreeHoursResponse(
    val availableDaysWeek: List<Int>,
    val calendar: List<Calendar>,
    val freeHours: String,
    val status: String
)

data class CalendarDays(
    val disabledDays: List<String>,
)