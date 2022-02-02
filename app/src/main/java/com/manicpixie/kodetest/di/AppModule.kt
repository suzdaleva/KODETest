package com.manicpixie.kodetest.di

import com.manicpixie.kodetest.common.Constants
import com.manicpixie.kodetest.data.remote.KodeApi
import com.manicpixie.kodetest.data.repository.KodeRepositoryImpl
import com.manicpixie.kodetest.domain.repository.KodeRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun providePaprikaApi(): KodeApi {
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(KodeApi::class.java)
    }

    @Provides
    @Singleton
    fun provideKodeRepository(api: KodeApi): KodeRepository {
        return KodeRepositoryImpl(api)
    }
}


