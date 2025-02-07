package com.alok.groww.Core.di

import com.alok.groww.Core.data.RetrofitInstance
import com.alok.groww.Core.utils.StockRepository
import com.alok.groww.Explore.data.repositoryImpl.SearchRepositoryImpl
import com.alok.groww.Explore.data.repositoryImpl.StocksOverviewRepositoryImpl
import com.alok.groww.Explore.data.repositoryImpl.StocksRepositoryImpl
import com.alok.groww.Explore.data.source.remote.ExploreApiService
import com.alok.groww.Explore.domain.models.StockOverviewData
import com.alok.groww.Explore.domain.repository.StocksOverviewRepository
import com.alok.groww.Explore.domain.repository.StocksRepository
import com.alok.groww.Search.domain.repository.SearchRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
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

    @Singleton
    @Provides
    fun provideStockRepository(): StocksRepository = StocksRepositoryImpl()

    @Singleton
    @Provides
    fun provideStockOverviewRepository(): StocksOverviewRepository = StocksOverviewRepositoryImpl()

    @Singleton
    @Provides
    fun provideSearchRepository(): SearchRepository = SearchRepositoryImpl()



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