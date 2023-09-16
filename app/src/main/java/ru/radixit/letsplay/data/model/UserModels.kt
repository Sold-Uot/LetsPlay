package ru.radixit.letsplay.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey




data class User(
    val id: Int,
    val name: String? = null,
    val photo: Photo? = null,
    val surname: String? = null,
    val userType: String? = null,
    val username: String? = null
)
@Entity
data class UserEntity (
    @PrimaryKey
    val id: Int,
    val name: String? = null,
    val photo: PhotoEntity? = null,
    val surname: String? = null,
    val userType: String? = null,
    val username: String? = null
    )