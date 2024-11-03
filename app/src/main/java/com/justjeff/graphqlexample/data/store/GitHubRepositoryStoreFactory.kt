package com.justjeff.graphqlexample.data.store

import com.apollographql.apollo.ApolloClient
import com.justjeff.graphqlexample.data.db.GitHubRepositoryDao
import com.justjeff.graphqlexample.data.mapper.GitHubRepositoryEntityMapper
import com.justjeff.graphqlexample.data.mapper.GitHubRepositoryMapper
import com.justjeff.graphqlexample.data.model.GitHubRepository
import com.justjeff.graphqlexample.data.model.GitHubRepositoryParams
import com.justjeff.graphqlexample.models.RepositoryQuery
import com.justjeff.graphqlexample.network.map
import kotlinx.coroutines.flow.map
import org.mobilenativefoundation.store.store5.Fetcher
import org.mobilenativefoundation.store.store5.SourceOfTruth
import org.mobilenativefoundation.store.store5.Store
import org.mobilenativefoundation.store.store5.StoreBuilder
import javax.inject.Inject

typealias GitHubRepositoryStore = Store<GitHubRepositoryParams, GitHubRepository>

internal class GitHubRepositoryStoreFactory @Inject constructor(
    private val client: ApolloClient,
    private val mapper: GitHubRepositoryMapper,
    private val dao: GitHubRepositoryDao,
    private val entityMapper: GitHubRepositoryEntityMapper,
) {
    fun create(): GitHubRepositoryStore =
        StoreBuilder.from(
            fetcher = createFetcher(),
            sourceOfTruth = createSourceOfTruth(),
        ).build()

    private fun createFetcher(): Fetcher<GitHubRepositoryParams, GitHubRepository> {
        return Fetcher.of("GraphQL") { params ->
            val request = RepositoryQuery(params.name, params.owner)
            client.query(request).execute().map(mapper::map)!!
        }
    }

    private fun createSourceOfTruth(): SourceOfTruth<GitHubRepositoryParams, GitHubRepository, GitHubRepository> {
        return SourceOfTruth.of(
            reader = { dao.findByNameAndOwner(it.name, it.owner).map(entityMapper::toResult) },
            writer = { _, repo -> dao.insert(entityMapper.toEntity(repo)) },
            delete = { dao.deleteByNameAndOwner(it.name, it.owner) },
        )
    }
}