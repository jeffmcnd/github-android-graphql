package com.justjeff.graphqlexample

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.ApolloRequest
import com.justjeff.graphqlexample.core.Result
import com.justjeff.graphqlexample.core.asResult
import com.justjeff.graphqlexample.models.RepositoryQuery
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val client: ApolloClient) : ViewModel() {
    val state: StateFlow<MainUiState> = mainUiState()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = MainUiState("Loading..."),
        )

    private fun mainUiState(): Flow<MainUiState> =
        repositoryDescription("github-react", "jeffmcnd")
            .asResult()
            .map { result ->
                when (result) {
                    is Result.Loading -> MainUiState("Loading...")

                    is Result.Success ->
                        MainUiState(result.data?.description ?: "No description specified.")

                    is Result.Error -> MainUiState("Unexpected error. Try again.")
                }
            }

    private fun repositoryDescription(
        name: String,
        owner: String,
    ): Flow<RepositoryQuery.Repository?> {
        val query = RepositoryQuery(name, owner)
        val request = ApolloRequest.Builder(query).build()
        return client.executeAsFlow(request).map { response ->
            val data = response.data
            val error = response.errors?.firstOrNull()
            val exception = response.exception
            when {
                data != null -> data.repository
                error != null -> throw Exception(error.message)
                exception != null -> throw exception
                else -> throw Exception("Unexpected state.")
            }
        }
    }
}