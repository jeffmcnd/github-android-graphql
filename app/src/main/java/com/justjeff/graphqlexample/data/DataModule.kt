package com.justjeff.graphqlexample.data

import android.content.Context
import androidx.room.Room
import com.justjeff.graphqlexample.data.db.AppDatabase
import com.justjeff.graphqlexample.data.db.GitHubRepositoryDao
import com.justjeff.graphqlexample.data.repo.GitHubRepositoryRepository
import com.justjeff.graphqlexample.data.repo.GitHubRepositoryRepositoryImpl
import com.justjeff.graphqlexample.data.store.GitHubRepositoryStore
import com.justjeff.graphqlexample.data.store.GitHubRepositoryStoreFactory
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class DataModule {
    @Binds
    @Singleton
    abstract fun bindGitHubRepositoryRepository(
        impl: GitHubRepositoryRepositoryImpl,
    ): GitHubRepositoryRepository
}

@Module
@InstallIn(SingletonComponent::class)
internal object DataProvidesModule {
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context, ): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "app_database").build()

    @Provides
    @Singleton
    fun provideGitHubRepositoryDao(db: AppDatabase): GitHubRepositoryDao = db.gitHubRepositoryDao()

    @Provides
    @Singleton
    fun provideGitHubRepositoryStore(
        factory: GitHubRepositoryStoreFactory,
    ): GitHubRepositoryStore = factory.create()
}
