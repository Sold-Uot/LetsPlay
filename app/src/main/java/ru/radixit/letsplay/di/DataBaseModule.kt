package ru.radixit.letsplay.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ru.radixit.letsplay.LetsPlayApp
import ru.radixit.letsplay.data.database.AppDatabase
import ru.radixit.letsplay.data.database.UserDao
import javax.inject.Singleton
@InstallIn(SingletonComponent::class)
@Module
object DataBaseModule {
    @Provides
    fun provideAppDatabase(@ApplicationContext appContext: Context): AppDatabase {
        return Room.databaseBuilder(
            appContext,
            AppDatabase::class.java, "my-db"
        ).build()
    }

    @Provides
    fun provideUserDao(database: AppDatabase): UserDao {
        return database.userDao

    }
}