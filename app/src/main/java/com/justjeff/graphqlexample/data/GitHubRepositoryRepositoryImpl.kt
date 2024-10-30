package com.justjeff.graphqlexample.data

import com.apollographql.apollo.ApolloClient
import com.justjeff.graphqlexample.core.Output
import com.justjeff.graphqlexample.core.asOutput
import com.justjeff.graphqlexample.models.RepositoryQuery
import kotlinx.coroutines.flow.Flow
import org.mobilenativefoundation.store.store5.Fetcher
import org.mobilenativefoundation.store.store5.StoreBuilder
import org.mobilenativefoundation.store.store5.StoreReadRequest
import javax.inject.Inject

internal class GitHubRepositoryRepositoryImpl @Inject constructor(
    private val client: ApolloClient,
    private val mapper: GitHubRepositoryMapper,
) : GitHubRepositoryRepository {

    private val store = StoreBuilder.from<GitHubRepositoryParams, GitHubRepositoryResult>(
        fetcher = Fetcher.of { params -> fetcher(params) }
    ).build()

    override fun getRepository(params: GitHubRepositoryParams): Flow<Output<GitHubRepositoryResult>> {
        val request = StoreReadRequest.cached(params, refresh = true)
        return store.stream(request).asOutput()
    }

    private suspend fun fetcher(params: GitHubRepositoryParams): GitHubRepositoryResult {
        val request = RepositoryQuery(params.name, params.owner)
        val response = client.query(request).execute()
        val data = response.data
        val error = response.errors?.firstOrNull()
        val exception = response.exception
        return when {
            data != null -> mapper.map(data)
            error != null -> throw Exception(error.message)
            exception != null -> throw exception
            else -> throw Exception("Unexpected state.")
        }
    }
}