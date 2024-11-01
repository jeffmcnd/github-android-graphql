package com.justjeff.graphqlexample.data.repo

import com.justjeff.graphqlexample.core.Output
import com.justjeff.graphqlexample.data.model.GitHubRepository
import com.justjeff.graphqlexample.data.model.GitHubRepositoryParams
import kotlinx.coroutines.flow.Flow

interface GitHubRepositoryRepository {
    fun getRepository(params: GitHubRepositoryParams): Flow<Output<GitHubRepository>>
}