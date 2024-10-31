package com.justjeff.graphqlexample.core

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.ApolloResponse
import com.apollographql.apollo.api.Operation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.mobilenativefoundation.store.store5.StoreReadResponse
import org.mobilenativefoundation.store.store5.StoreReadResponseOrigin

sealed interface Output<out T> {
    val origin: OutputOrigin

    data class Loading(override val origin: OutputOrigin) : Output<Nothing>

    data class Success<T>(val data: T, override val origin: OutputOrigin) : Output<T>

    data class NothingNew(override val origin: OutputOrigin) : Output<Nothing>

    sealed class Error : Output<Nothing> {
        data class Exception(val error: Throwable, override val origin: OutputOrigin) : Error()
        data class Message(val message: String, override val origin: OutputOrigin) : Error()
    }
}

sealed class OutputOrigin {
    data object Cache : OutputOrigin()
    data object SourceOfTruth : OutputOrigin()
    data class Fetcher(val name: String? = null) : OutputOrigin()
}

fun <T> Flow<StoreReadResponse<T>>.asOutput(): Flow<Output<T>> = map { response ->
    val origin = response.origin.asOutputOrigin()
    when (response) {
        is StoreReadResponse.Data -> Output.Success(response.value, origin)
        is StoreReadResponse.Error.Exception -> Output.Error.Exception(response.error, origin)
        is StoreReadResponse.Error.Message -> Output.Error.Message(response.message, origin)
        is StoreReadResponse.Loading -> Output.Loading(origin)
        is StoreReadResponse.NoNewData -> Output.NothingNew(origin)
    }
}

fun StoreReadResponseOrigin.asOutputOrigin(): OutputOrigin =
    when (this) {
        is StoreReadResponseOrigin.Cache -> OutputOrigin.Cache
        is StoreReadResponseOrigin.SourceOfTruth -> OutputOrigin.SourceOfTruth
        is StoreReadResponseOrigin.Fetcher -> OutputOrigin.Fetcher(name)
    }
