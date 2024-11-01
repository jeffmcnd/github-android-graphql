package com.justjeff.graphqlexample.data

import com.justjeff.graphqlexample.data.repo.GitHubRepositoryRepository
import com.justjeff.graphqlexample.data.repo.GitHubRepositoryRepositoryImpl
import com.justjeff.graphqlexample.data.store.GitHubRepositoryStore
import com.justjeff.graphqlexample.data.store.GitHubRepositoryStoreFactory
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
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
    fun provideGitHubRepositoryStore(
        factory: GitHubRepositoryStoreFactory,
    ): GitHubRepositoryStore = factory.create()
}
