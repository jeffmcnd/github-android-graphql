package com.justjeff.graphqlexample.data

import com.apollographql.apollo.ApolloClient
import com.justjeff.graphqlexample.models.RepositoryQuery
import com.justjeff.graphqlexample.network.map
import org.mobilenativefoundation.store.store5.Fetcher
import org.mobilenativefoundation.store.store5.Store
import org.mobilenativefoundation.store.store5.StoreBuilder
import javax.inject.Inject

typealias GitHubRepositoryStore = Store<GitHubRepositoryParams, GitHubRepositoryResult>

internal class GitHubRepositoryStoreFactory @Inject constructor(
    private val client: ApolloClient,
    private val mapper: GitHubRepositoryMapper,
) {
    fun create(): GitHubRepositoryStore =
        StoreBuilder.from(fetcher = createFetcher()).build()

    private fun createFetcher(): Fetcher<GitHubRepositoryParams, GitHubRepositoryResult> {
        return Fetcher.of { params ->
            val request = RepositoryQuery(params.name, params.owner)
            client.query(request).execute().map(mapper::map)
        }
    }
}