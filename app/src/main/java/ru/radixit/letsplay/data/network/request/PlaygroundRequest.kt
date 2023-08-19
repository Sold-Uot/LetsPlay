package ru.radixit.letsplay.data.network.request

import ru.radixit.letsplay.data.model.FieldSize
import ru.radixit.letsplay.data.model.Geocode
import ru.radixit.letsplay.data.model.Schedule

data class CreatePlaygroundRequest(
    val ball: String?,
    val changingRooms: String?,
    val coating: Int?,
    val fieldSize: FieldSize?,
    val geocode: Geocode?,
    val lighting: String?,
    val manishki: String?,
    val paymentByTransfer: Boolean?,
    val phone: String?,
    val price: String?,
    val schedule: List<Schedule?>?,
    val shower: String?,
    val studdedCleats: Boolean?,
    val title: String?
)