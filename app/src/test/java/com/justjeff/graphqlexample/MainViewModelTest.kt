package com.justjeff.graphqlexample

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.annotations.ApolloExperimental
import com.apollographql.apollo.api.Error
import com.apollographql.apollo.testing.QueueTestNetworkTransport
import com.apollographql.apollo.testing.enqueueTestNetworkError
import com.apollographql.apollo.testing.enqueueTestResponse
import com.justjeff.graphqlexample.models.RepositoryQuery
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Rule
import org.junit.Test

@OptIn(ApolloExperimental::class)
class MainViewModelTest {
    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private val repository = "github-react"
    private val query = RepositoryQuery(repository, "jeffmcnd")

    private val client = ApolloClient.Builder()
        .networkTransport(QueueTestNetworkTransport())
        .build()

    @Test
    fun `state - Success emits Success`() = runTest(dispatcherRule.dispatcher) {
        val subject = getSubject()
        val data = RepositoryQuery.Data(RepositoryQuery.Repository("description"))
        client.enqueueTestResponse(query, data, errors = null)
        subject.state.test {
            Assert.assertEquals(MainUiState.EmptyQuery, awaitItem())
            subject.onSearchQueryChanged(repository)
            Assert.assertEquals(MainUiState.Loading, awaitItem())
            Assert.assertEquals(MainUiState.Success("description"), awaitItem())
        }
    }

    @Test
    fun `state - Error emits LoadFailed`() = runTest(dispatcherRule.dispatcher) {
        val subject = getSubject()
        val message = "No repository found."
        val errors = listOf(Error.Builder(message).build())
        client.enqueueTestResponse(operation = query, data = null, errors = errors)
        subject.state.test {
            Assert.assertEquals(MainUiState.EmptyQuery, awaitItem())
            subject.onSearchQueryChanged(repository)
            Assert.assertEquals(MainUiState.Loading, awaitItem())
            Assert.assertEquals(MainUiState.LoadFailed, awaitItem())
        }
    }

    @Test
    fun `state - Null data and error emits LoadFailed`() = runTest(dispatcherRule.dispatcher) {
        val subject = getSubject()
        client.enqueueTestResponse(query, null, null)
        subject.state.test {
            Assert.assertEquals(MainUiState.EmptyQuery, awaitItem())
            subject.onSearchQueryChanged(repository)
            Assert.assertEquals(MainUiState.Loading, awaitItem())
            Assert.assertEquals(MainUiState.LoadFailed, awaitItem())
        }
    }

    @Test
    fun `state - Network error emits LoadFailed`() = runTest(dispatcherRule.dispatcher) {
        val subject = getSubject()
        client.enqueueTestNetworkError()
        subject.state.test {
            Assert.assertEquals(MainUiState.EmptyQuery, awaitItem())
            subject.onSearchQueryChanged(repository)
            Assert.assertEquals(MainUiState.Loading, awaitItem())
            Assert.assertEquals(MainUiState.LoadFailed, awaitItem())
        }
    }

    private fun getSubject(): MainViewModel = MainViewModel(SavedStateHandle(), client)
}