package ru.radixit.letsplay.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import ru.radixit.letsplay.data.model.PhotoEntity
import ru.radixit.letsplay.data.model.UserEntity

@Database(
    version = 1,
    entities = [UserEntity::class ],
    exportSchema = true
)
abstract class AppDatabase :RoomDatabase() {
    public abstract val userDao: UserDao

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