package com.justjeff.graphqlexample.data.repo

import com.justjeff.graphqlexample.core.Output
import com.justjeff.graphqlexample.core.asOutput
import com.justjeff.graphqlexample.data.model.GitHubRepository
import com.justjeff.graphqlexample.data.model.GitHubRepositoryParams
import com.justjeff.graphqlexample.data.store.GitHubRepositoryStore
import kotlinx.coroutines.flow.Flow
import org.mobilenativefoundation.store.store5.StoreReadRequest
import javax.inject.Inject

internal class GitHubRepositoryRepositoryImpl @Inject constructor(
    private val store: GitHubRepositoryStore,
) : GitHubRepositoryRepository {
    override fun getRepository(params: GitHubRepositoryParams): Flow<Output<GitHubRepository>> {
        val request = StoreReadRequest.cached(params, refresh = false)
        return store.stream(request).asOutput()
    }
}