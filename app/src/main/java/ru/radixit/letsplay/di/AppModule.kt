package ru.radixit.letsplay.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.radixit.letsplay.data.local.shared_pref.SharedPrefRepositoryImpl
import ru.radixit.letsplay.data.repository.*
import ru.radixit.letsplay.domain.global.ResourceManager
import ru.radixit.letsplay.domain.repository.*
import ru.radixit.letsplay.presentation.global.AndroidResourceManager
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    @Singleton
    abstract fun bindResourceManager(resourceManager: AndroidResourceManager): ResourceManager

    @Binds
    @Singleton
    abstract fun bindAuthRepository(repository: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    abstract fun bindPlaygroundRepository(repository: PlaygroundRepositoryImpl): PlaygroundRepository

    @Binds
    @Singleton
    abstract fun bindProfileRepository(repository: ProfileRepositoryImpl): ProfileRepository

    @Binds
    @Singleton
    abstract fun bindSharedPrefRepository(repository: SharedPrefRepositoryImpl): SharedPrefRepository

    @Binds
    @Singleton
    abstract fun bindEventRepository(repository: EventRepositoryImpl): EventRepository

    @Binds
    @Singleton
    abstract fun bindChatRepository(repository: ChatRepositoryImpl): ChatRepository

    @Binds
    @Singleton
    abstract fun bindNotificationRepository(repository: NotificationRepositoryImpl): NotificationRepository

    @Binds
    @Singleton
    abstract fun bindUserRepository(repository: UserRepositoryImpl): UserRepository

}