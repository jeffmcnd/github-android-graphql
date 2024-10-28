package com.justjeff.graphqlexample.data

import kotlinx.coroutines.flow.Flow

interface GitHubRepositoryRepository {
    fun getRepository(name: String, owner: String): Flow<GitHubRepository?>
}