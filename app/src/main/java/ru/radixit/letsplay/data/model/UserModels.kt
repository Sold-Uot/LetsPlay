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
    @PrimaryKey(autoGenerate = false)
    val id_user: Int,
    val name: String? = null,
    val photo_id: Int? = null,
    val photo_url : String? = null,
    val surname: String? = null,
    val userType: String? = null,
    val username: String? = null
    )