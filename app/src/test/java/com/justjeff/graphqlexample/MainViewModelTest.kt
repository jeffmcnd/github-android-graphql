package com.justjeff.graphqlexample

import app.cash.turbine.test
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.annotations.ApolloExperimental
import com.apollographql.apollo.testing.QueueTestNetworkTransport
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

    @Test
    fun `initialize - Fetches description`() = runTest(dispatcherRule.dispatcher) {
        val query = RepositoryQuery("github-react", "jeffmcnd")
        val data = RepositoryQuery.Data(RepositoryQuery.Repository("description"))
        client.enqueueTestResponse(query, data)

        subject.state.test {
            Assert.assertEquals(MainUiState("Loading..."), awaitItem())
            subject.initialize()
            Assert.assertEquals(MainUiState("description"), awaitItem())
        }
    }
}