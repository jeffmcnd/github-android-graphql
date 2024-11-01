package com.justjeff.graphqlexample.data.mapper

import com.justjeff.graphqlexample.data.model.GitHubRepository
import com.justjeff.graphqlexample.data.model.GitHubRepositoryEntity
import javax.inject.Inject

internal class GitHubRepositoryEntityMapper @Inject constructor() {
    fun toResult(entity: GitHubRepositoryEntity?): GitHubRepository? {
        if (entity == null) return null
        return GitHubRepository(entity.name, entity.owner, entity.description)
    }
    
    fun toEntity(repo: GitHubRepository): GitHubRepositoryEntity {
        return GitHubRepositoryEntity(repo.name, repo.owner, repo.description)
    }
}