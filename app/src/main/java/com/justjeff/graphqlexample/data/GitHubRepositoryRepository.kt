package com.justjeff.graphqlexample.data

import com.justjeff.graphqlexample.core.Output
import kotlinx.coroutines.flow.Flow

interface GitHubRepositoryRepository {
    fun getRepository(params: GitHubRepositoryParams): Flow<Output<GitHubRepositoryResult>>
}