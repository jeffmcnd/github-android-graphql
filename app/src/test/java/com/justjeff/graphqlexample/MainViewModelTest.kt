package com.justjeff.graphqlexample

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

    private val client = ApolloClient.Builder()
        .networkTransport(QueueTestNetworkTransport())
        .build()
    private val subject = MainViewModel(client)

    private val query = RepositoryQuery("github-react", "jeffmcnd")

    @Test
    fun `initialize - Fetches description`() = runTest(dispatcherRule.dispatcher) {
        val data = RepositoryQuery.Data(RepositoryQuery.Repository("description"))
        client.enqueueTestResponse(query, data, errors = null)
        subject.state.test {
            Assert.assertEquals(MainUiState("Loading..."), awaitItem())
            Assert.assertEquals(MainUiState("description"), awaitItem())
        }
    }

    @Test
    fun `initialize - Error sets text to first message`() = runTest(dispatcherRule.dispatcher) {
        val message = "No repository found."
        val errors = listOf(Error.Builder(message).build())
        client.enqueueTestResponse(operation = query, data = null, errors = errors)
        subject.state.test {
            Assert.assertEquals(MainUiState("Loading..."), awaitItem())
            Assert.assertEquals(MainUiState(message), awaitItem())
        }
    }

    @Test
    fun `initialize - Null data and error sets text`() = runTest(dispatcherRule.dispatcher) {
        client.enqueueTestResponse(query, null, null)
        subject.state.test {
            Assert.assertEquals(MainUiState("Loading..."), awaitItem())
            Assert.assertEquals(MainUiState("Unexpected error. Try again."), awaitItem())
        }
    }

    @Test
    fun `initialize - Network error sets text`() = runTest(dispatcherRule.dispatcher) {
        client.enqueueTestNetworkError()
        subject.state.test {
            Assert.assertEquals(MainUiState("Loading..."), awaitItem())
            Assert.assertEquals(MainUiState("Unexpected error. Try again."), awaitItem())
        }
    }
}