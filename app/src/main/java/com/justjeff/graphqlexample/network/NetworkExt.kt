package com.justjeff.graphqlexample.network

import com.apollographql.apollo.api.ApolloResponse
import com.apollographql.apollo.api.Operation

fun <T: Operation.Data, U> ApolloResponse<T>.map(block: (T) -> U): U {
    val data = this.data
    val error = this.errors?.firstOrNull()
    val exception = this.exception
    return when {
        data != null -> block(data)
        error != null -> throw Exception(error.message)
        exception != null -> throw exception
        else -> throw IllegalStateException("Unexpected ApolloResponse state.")
    }
}
