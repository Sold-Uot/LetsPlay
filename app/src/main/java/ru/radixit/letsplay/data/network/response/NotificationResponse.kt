package ru.radixit.letsplay.data.network.response

import ru.radixit.letsplay.data.model.Notification

data class NotificationResponse(
    val list: List<Notification>,
    val pages: Int,
    val status: String,
    val total: Int
)