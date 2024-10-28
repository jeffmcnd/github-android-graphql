package com.justjeff.graphqlexample.data

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.ApolloRequest
import com.justjeff.graphqlexample.models.RepositoryQuery
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class GitHubRepositoryRepositoryImpl @Inject constructor(
    private val client: ApolloClient,
    private val mapper: GitHubRepositoryMapper,
) : GitHubRepositoryRepository {
    override fun getRepository(name: String, owner: String): Flow<GitHubRepository?> {
        val query = RepositoryQuery(name, owner)
        val request = ApolloRequest.Builder(query).build()
        return client.executeAsFlow(request).map { response ->
            val data = response.data
            val error = response.errors?.firstOrNull()
            val exception = response.exception
            when {
                data != null -> mapper.map(data)
                error != null -> throw Exception(error.message)
                exception != null -> throw exception
                else -> throw Exception("Unexpected state.")
            }
        }
    }
}