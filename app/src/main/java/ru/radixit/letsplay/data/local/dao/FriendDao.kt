package ru.radixit.letsplay.data.local.dao

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ru.radixit.letsplay.data.model.FriendEntity
import ru.radixit.letsplay.data.model.UserEntity

interface FriendDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addUser(user : FriendEntity)

    @Delete
    suspend fun removeUser(use : FriendEntity)

    @Query("SELECT * FROM friend_table")
    suspend fun getALlUser(): List<FriendEntity>

}