package com.justjeff.graphqlexample

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.justjeff.graphqlexample.core.Output
import com.justjeff.graphqlexample.core.OutputOrigin
import com.justjeff.graphqlexample.data.GitHubRepository
import com.justjeff.graphqlexample.data.GitHubRepositoryParams
import com.justjeff.graphqlexample.data.GitHubRepositoryRepository
import com.justjeff.graphqlexample.data.GitHubRepositoryResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test

class MainViewModelTest {
    private val searchQuery = "github-react"

    @Test
    fun `state - Null emits empty Success`() = runTest {
        val result = GitHubRepositoryResult(null)
        val repo = gitHubRepositoryRepository {
            emit(Output.Loading(OutputOrigin.Fetcher()))
            emit(Output.Success(result, OutputOrigin.Fetcher()))
        }
        val subject = getSubject(backgroundScope, repo)
        subject.state.test {
            Assert.assertEquals(MainUiState.EmptyQuery, awaitItem())
            subject.onSearchQueryChanged(searchQuery)
            Assert.assertEquals(MainUiState.Loading, awaitItem())
            Assert.assertEquals(MainUiState.Success(""), awaitItem())
        }
    }

    @Test
    fun `state - Success emits Success`() = runTest {
        val repository = GitHubRepository("test", "test", "test")
        val result = GitHubRepositoryResult(repository)
        val repo = gitHubRepositoryRepository {
            emit(Output.Loading(OutputOrigin.Fetcher()))
            emit(Output.Success(result, OutputOrigin.Fetcher()))
        }
        val subject = getSubject(backgroundScope, repo)
        subject.state.test {
            Assert.assertEquals(MainUiState.EmptyQuery, awaitItem())
            subject.onSearchQueryChanged(searchQuery)
            Assert.assertEquals(MainUiState.Loading, awaitItem())
            Assert.assertEquals(MainUiState.Success("test"), awaitItem())
        }
    }

    @Test
    fun `state - Exception emits LoadFailed`() = runTest {
        val repo = gitHubRepositoryRepository {
            emit(Output.Loading(OutputOrigin.Fetcher()))
            emit(Output.Error.Exception(Exception(), OutputOrigin.Fetcher()))
        }
        val subject = getSubject(backgroundScope, repo)
        subject.state.test {
            Assert.assertEquals(MainUiState.EmptyQuery, awaitItem())
            subject.onSearchQueryChanged(searchQuery)
            Assert.assertEquals(MainUiState.Loading, awaitItem())
            Assert.assertEquals(MainUiState.LoadFailed, awaitItem())
        }
    }

    private fun gitHubRepositoryRepository(block: suspend FlowCollector<Output<GitHubRepositoryResult>>.() -> Unit) =
        object : GitHubRepositoryRepository {
            override fun getRepository(params: GitHubRepositoryParams): Flow<Output<GitHubRepositoryResult>> =
                flow(block)
        }

    private fun getSubject(
        scope: CoroutineScope,
        repo: GitHubRepositoryRepository,
    ): MainViewModel = MainViewModel(SavedStateHandle(), repo, scope)
}