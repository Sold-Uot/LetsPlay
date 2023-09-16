package ru.radixit.letsplay.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import ru.radixit.letsplay.data.model.UserEntity
@Dao
interface UserDao {
    @Insert
    suspend fun addUser(user : UserEntity)

    @Delete
    suspend fun removeUser(use :UserEntity)

    @Query("SELECT * FROM userentity")
    suspend fun getALlUser(): List<UserEntity>


}