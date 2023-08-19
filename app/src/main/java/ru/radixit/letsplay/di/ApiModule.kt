package ru.radixit.letsplay.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.create
import ru.radixit.letsplay.data.network.api.*
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ApiModule {

    @Provides
    @Singleton
    fun provideAuthApi(retrofit: Retrofit): AuthApi = retrofit.create()

    @Provides
    @Singleton
    fun provideChatApi(retrofit: Retrofit): ChatApi = retrofit.create()

    @Provides
    @Singleton
    fun provideCommentApi(retrofit: Retrofit): CommentApi = retrofit.create()

    @Provides
    @Singleton
    fun provideEventApi(retrofit: Retrofit): EventApi = retrofit.create()

    @Provides
    @Singleton
    fun provideFriendApi(retrofit: Retrofit): FriendApi = retrofit.create()

    @Provides
    @Singleton
    fun provideNotificationApi(retrofit: Retrofit): NotificationApi = retrofit.create()

    @Provides
    @Singleton
    fun providePlaygroundApi(retrofit: Retrofit): PlaygroundsApi = retrofit.create()

    @Provides
    @Singleton
    fun provideProfileApi(retrofit: Retrofit): ProfileApi = retrofit.create()

    @Provides
    @Singleton
    fun provideTeamApi(retrofit: Retrofit): TeamApi = retrofit.create()

    @Provides
    @Singleton
    fun provideUserApi(retrofit: Retrofit): UserApi = retrofit.create()

}