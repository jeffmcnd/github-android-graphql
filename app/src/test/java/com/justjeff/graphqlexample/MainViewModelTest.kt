package com.justjeff.graphqlexample

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.apollographql.apollo.annotations.ApolloExperimental
import com.justjeff.graphqlexample.data.GitHubRepository
import com.justjeff.graphqlexample.data.GitHubRepositoryRepository
import com.justjeff.graphqlexample.models.RepositoryQuery
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test

class MainViewModelTest {
    private val searchQuery = "github-react"
    private val repo = object : GitHubRepositoryRepository {
        var flow: Flow<GitHubRepository?> = flowOf(null)
        override fun getRepository(name: String, owner: String): Flow<GitHubRepository?> = flow
    }

    @Test
    fun `state - Null emits empty Success`() = runTest {
        val subject = getSubject(backgroundScope)
        subject.state.test {
            Assert.assertEquals(MainUiState.EmptyQuery, awaitItem())
            subject.onSearchQueryChanged(searchQuery)
            Assert.assertEquals(MainUiState.Loading, awaitItem())
            Assert.assertEquals(MainUiState.Success(""), awaitItem())
        }
    }

    @Test
    fun `state - Success emits Success`() = runTest {
        val subject = getSubject(backgroundScope)
        repo.flow = flowOf(GitHubRepository("test", "test", "test"))
        subject.state.test {
            Assert.assertEquals(MainUiState.EmptyQuery, awaitItem())
            subject.onSearchQueryChanged(searchQuery)
            Assert.assertEquals(MainUiState.Loading, awaitItem())
            Assert.assertEquals(MainUiState.Success("test"), awaitItem())
        }
    }

    @Test
    fun `state - Exception emits LoadFailed`() = runTest {
        val subject = getSubject(backgroundScope)
        repo.flow = flow { throw Exception() }
        subject.state.test {
            Assert.assertEquals(MainUiState.EmptyQuery, awaitItem())
            subject.onSearchQueryChanged(searchQuery)
            Assert.assertEquals(MainUiState.Loading, awaitItem())
            Assert.assertEquals(MainUiState.LoadFailed, awaitItem())
        }
    }

    private fun getSubject(scope: CoroutineScope): MainViewModel =
        MainViewModel(SavedStateHandle(), repo, scope)
}