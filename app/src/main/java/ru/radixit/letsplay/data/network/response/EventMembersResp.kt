package ru.radixit.letsplay.data.network.response

data class EventMembersResp(
    val list: List<Member>,
    val status: String
){

    data class Member(
        val id: Int,
        val name: String,
        val photo: Any,
        val surname: Any,
        val username: String
    )
}