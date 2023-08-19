package ru.radixit.letsplay.data.model

data class NotificationModel(
    val list: List<Notification>,
    val pages: Int,
    val status: String,
    val total: Int
)
data class Notification(
    val createdAt: String,
    val id: Int,
    val photo: String?,
    val time: String,
    val title: String?,
    val type: Type
)

data class Type(
    val label: String,
    val value: Int
)