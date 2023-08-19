package ru.radixit.letsplay.data.network.response

import ru.radixit.letsplay.data.model.Error

data class EventDescriptionResponse(
    val address: String? = null,
    val comment: String? = null,
    val gameLevel: String? = null,
    val id: Int? = null,
    val players: String? = null,
    val playgroundId: Int? = null,
    val playgroundTitle: String? = null,
    val start: String? = null,
    val status: String? = null,
    val title: String? = null,
    val isOwner: Boolean? = null,
    val isRequestSended: Boolean? = null
)

data class EventForMap(
    val address: String,
    val gameLevel: String,
    val id: Int,
    val players: String,
    val playgroundId: Int,
    val preview: String? = null,
    val start: String,
    val status: String,
    val title: String
)

data class CreateEventResponse(
    val eventId: Int,
    val message: String,
    val status: String,
    val errors: List<Error>
)

data class JoinEventResponse(
    val status: String,
    val message: String
)