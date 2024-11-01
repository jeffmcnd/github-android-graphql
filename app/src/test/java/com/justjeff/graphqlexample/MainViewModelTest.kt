package com.justjeff.graphqlexample

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.justjeff.graphqlexample.core.Output
import com.justjeff.graphqlexample.core.OutputOrigin
import com.justjeff.graphqlexample.data.model.GitHubRepository
import com.justjeff.graphqlexample.data.model.GitHubRepositoryParams
import com.justjeff.graphqlexample.data.repo.GitHubRepositoryRepository
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
    fun `state - Success emits Success`() = runTest {
        val repository = GitHubRepository("test", "test", "test")
        val repo = gitHubRepositoryRepository {
            emit(Output.Loading(OutputOrigin.Fetcher()))
            emit(Output.Success(repository, OutputOrigin.Fetcher()))
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

    private fun gitHubRepositoryRepository(block: suspend FlowCollector<Output<GitHubRepository>>.() -> Unit) =
        object : GitHubRepositoryRepository {
            override fun getRepository(params: GitHubRepositoryParams): Flow<Output<GitHubRepository>> =
                flow(block)
        }

    private fun getSubject(
        scope: CoroutineScope,
        repo: GitHubRepositoryRepository,
    ): MainViewModel = MainViewModel(SavedStateHandle(), repo, scope)
}