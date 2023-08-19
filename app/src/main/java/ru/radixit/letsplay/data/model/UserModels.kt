package ru.radixit.letsplay.data.model

data class User(
    val id: Int,
    val name: String? = null,
    val photo: Photo? = null,
    val surname: Any? = null,
    val userType: String? = null,
    val username: String? = null
)