package ru.radixit.letsplay.data.network.response

import ru.radixit.letsplay.data.model.Photo

data class EventMembersResp(
    val list: List<Member>,
    val status: String
){

    data class Member(
        val id: Int,
        val name: String,
        val photo: Photo?,
        val surname: Any,
        val username: String
    )
}