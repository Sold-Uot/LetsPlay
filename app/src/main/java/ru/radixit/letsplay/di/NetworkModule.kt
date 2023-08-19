package ru.radixit.letsplay.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.radixit.letsplay.BuildConfig
import ru.radixit.letsplay.utils.AuthTokenInterceptor
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object NetworkModule {

    private const val NETWORK_CALL_TIMEOUT = 60

    @Provides
    @Singleton
    fun provideOkHttpClient(
        accessTokenInterceptor: AuthTokenInterceptor,
        httpLoggingInterceptor: HttpLoggingInterceptor,
    ): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(accessTokenInterceptor)
            .addInterceptor(httpLoggingInterceptor)
//            .connectTimeout(NETWORK_CALL_TIMEOUT.toLong(), TimeUnit.SECONDS)
//            .readTimeout(NETWORK_CALL_TIMEOUT.toLong(), TimeUnit.SECONDS)
//            .writeTimeout(NETWORK_CALL_TIMEOUT.toLong(), TimeUnit.SECONDS)
            .retryOnConnectionFailure(false)
            .build()

    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor =
        HttpLoggingInterceptor().apply {
            if (BuildConfig.DEBUG) {
                setLevel(HttpLoggingInterceptor.Level.BODY)
            }
        }
}