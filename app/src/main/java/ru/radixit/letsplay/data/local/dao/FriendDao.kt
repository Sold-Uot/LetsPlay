package ru.radixit.letsplay.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ru.radixit.letsplay.data.model.FriendEntity

@Dao
interface FriendDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addUser(user : FriendEntity)

    @Delete
    suspend fun removeUser(use : FriendEntity)

    @Query("SELECT * FROM FriendEntity")
    suspend fun getALlUser(): List<FriendEntity>

    @Query("SELECT COUNT(*) FROM FriendEntity")
    suspend fun getRowCount(): Int

}