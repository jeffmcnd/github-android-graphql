package com.justjeff.graphqlexample.network

import com.apollographql.apollo.api.ApolloResponse
import com.apollographql.apollo.api.Error
import com.apollographql.apollo.exception.DefaultApolloException
import com.benasher44.uuid.Uuid
import com.justjeff.graphqlexample.models.RepositoryQuery
import org.junit.Assert
import org.junit.Test

class NetworkExtKtTest {
    private val query = RepositoryQuery("test", "test")
    private val owner = RepositoryQuery.Owner("test")
    private val repo = RepositoryQuery.Repository("test", owner, "test")
    private val data = RepositoryQuery.Data(repo)

    @Test
    fun `ApolloResponse#map returns data`() {
        val response = ApolloResponse.Builder(query, Uuid.randomUUID())
            .data(data)
            .build()
        val result = response.map { it }
        Assert.assertEquals(data, result)
    }

    @Test(expected = Exception::class)
    fun `ApolloResponse#map throws exception from errors`() {
        val errors = listOf(Error.Builder("test").build())
        val response = ApolloResponse.Builder(query, Uuid.randomUUID())
            .errors(errors)
            .build()
        response.map { it }
    }

    @Test(expected = DefaultApolloException::class)
    fun `ApolloResponse#map throws exception`() {
        val response = ApolloResponse.Builder(query, Uuid.randomUUID())
            .exception(DefaultApolloException())
            .build()
        response.map { it }
    }

    @Test(expected = IllegalStateException::class)
    fun `ApolloResponse#map throws illegal state exception`() {
        val response = ApolloResponse.Builder(query, Uuid.randomUUID())
            .build()
        response.map { it }
    }
}