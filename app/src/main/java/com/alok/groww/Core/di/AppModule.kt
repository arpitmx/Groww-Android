package com.alok.groww.Core.di

import android.content.Context
import androidx.room.Room
import com.alok.groww.Core.data.RetrofitInstance
import com.alok.groww.Explore.data.source.remote.ExploreApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideAlphaVantageApi(): ExploreApiService {
        return RetrofitInstance.exploreApi
    }

//    @Singleton
//    @Provides
//    fun provideDatabase(@ApplicationContext appContext: Context): AppDatabase {
//        return Room.databaseBuilder(
//            appContext,
//            AppDatabase::class.java,
//            "app_database"
//        ).build()
//    }
//
//    @Singleton
//    @Provides
//    fun provideStockCacheDao(database: AppDatabase): StockCacheDao {
//        return database.stockCacheDao()
//    }
}