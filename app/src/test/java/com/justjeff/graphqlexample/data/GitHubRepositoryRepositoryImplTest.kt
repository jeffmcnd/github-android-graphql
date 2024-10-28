package com.justjeff.graphqlexample.data

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
import org.junit.Test

@OptIn(ApolloExperimental::class)
class GitHubRepositoryRepositoryImplTest {
    private val client = ApolloClient.Builder()
        .networkTransport(QueueTestNetworkTransport())
        .build()
    private val mapper = GitHubRepositoryMapper()
    private val query = RepositoryQuery("test", "test")

    @Test
    fun `getRepository - Success returns repository`() = runTest {
        val subject = getSubject()
        val owner = RepositoryQuery.Owner("test")
        val repository = RepositoryQuery.Repository("test", owner, "test")
        val data = RepositoryQuery.Data(repository)
        client.enqueueTestResponse(query, data)
        subject.getRepository("test", "test").test {
            val expected = GitHubRepository("test", "test", "test")
            Assert.assertEquals(expected, awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `getRepository - Errors throws exception`() = runTest {
        val subject = getSubject()
        val message = "No repository found."
        val errors = listOf(Error.Builder(message).build())
        client.enqueueTestResponse(query, errors = errors)
        subject.getRepository("test", "test").test {
            awaitError()
        }
    }

    @Test
    fun `getRepository - Null data and errors throws exception`() = runTest {
        val subject = getSubject()
        client.enqueueTestResponse(query, data = null, errors = null)
        subject.getRepository("test", "test").test {
            awaitError()
        }
    }

    @Test
    fun `getRepository - Network error throws exception`() = runTest {
        val subject = getSubject()
        client.enqueueTestNetworkError()
        subject.getRepository("test", "test").test {
            awaitError()
        }
    }

    private fun getSubject(): GitHubRepositoryRepository =
        GitHubRepositoryRepositoryImpl(client, mapper)
}