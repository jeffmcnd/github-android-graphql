package com.justjeff.graphqlexample.data.mapper

import com.justjeff.graphqlexample.data.model.GitHubRepository
import com.justjeff.graphqlexample.models.RepositoryQuery
import javax.inject.Inject

internal class GitHubRepositoryMapper @Inject constructor() {
    fun map(data: RepositoryQuery.Data): GitHubRepository? {
        val repo = data.repository ?: return null
        return GitHubRepository(repo.name, repo.owner.login, repo.description)
    }
}