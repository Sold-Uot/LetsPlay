package ru.radixit.letsplay.data.network.request

data class CreateEventRequest(
    val comment: String,
    val gameLevel: Int,
    val startKey: Int,
    val id: Int,
    val date: String,
    val endKey: Int,
    val offlinePlayers: Int,
    val players: Int,
    val status: Int,
    val title: String
)

data class JoinEventRequest(
    val position: Int
)