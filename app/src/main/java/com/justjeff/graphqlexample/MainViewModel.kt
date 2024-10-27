package com.justjeff.graphqlexample

import androidx.lifecycle.SavedStateHandle
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
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val client: ApolloClient,
) : ViewModel() {
    val searchQuery =
        savedStateHandle.getStateFlow(key = SEARCH_QUERY, initialValue = "")

    val state: StateFlow<MainUiState> = mainUiState()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = MainUiState.EmptyQuery,
        )

    fun onSearchQueryChanged(query: String) {
        savedStateHandle[SEARCH_QUERY] = query
    }

    private fun mainUiState(): Flow<MainUiState> =
        searchQuery
            .debounce(500)
            .flatMapLatest { query ->
                if (query.length < SEARCH_QUERY_MIN_LENGTH) {
                    flowOf(MainUiState.EmptyQuery)
                } else {
                    repositoryDescription(query, "jeffmcnd")
                        .asResult()
                        .map { result ->
                            when (result) {
                                is Result.Loading -> MainUiState.Loading

                                is Result.Success -> {
                                    val repository = result.data
                                    val desc = if (repository != null) {
                                        repository.description ?: "No description specified."
                                    } else {
                                        ""
                                    }
                                    MainUiState.Success(desc)
                                }

                                is Result.Error -> MainUiState.LoadFailed
                            }
                        }
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

private const val SEARCH_QUERY = "searchQuery"
private const val SEARCH_QUERY_MIN_LENGTH = 2