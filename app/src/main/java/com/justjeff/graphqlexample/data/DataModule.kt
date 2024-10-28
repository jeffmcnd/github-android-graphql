package com.justjeff.graphqlexample.data

import dagger.Binds
import dagger.Module
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