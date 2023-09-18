package ru.radixit.letsplay.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import ru.radixit.letsplay.data.local.dao.UserDao
import ru.radixit.letsplay.data.model.UserEntity

@Database(
    version = 1,
    entities = [UserEntity::class ],
    exportSchema = true
)
abstract class AppDatabase :RoomDatabase() {
    public abstract val userDao: UserDao

    fun getDatabase(context: Context): AppDatabase {
        // проверка на нулл
        if (INSTANCE == null) {
            synchronized(this) {
                INSTANCE = buildDatabase(context)
            }
        }
        return INSTANCE!!
    }
    companion object {

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
          // проверка на нулл
            if (INSTANCE == null) {
                synchronized(this) {
                    INSTANCE = buildDatabase(context)
                }
            }
            return INSTANCE!!
        }

        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "useres"
            )
                .build()
        }
    }
}