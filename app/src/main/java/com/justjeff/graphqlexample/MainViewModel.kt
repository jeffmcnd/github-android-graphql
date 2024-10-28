package com.justjeff.graphqlexample

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.justjeff.graphqlexample.core.Result
import com.justjeff.graphqlexample.core.asResult
import com.justjeff.graphqlexample.data.GitHubRepository
import com.justjeff.graphqlexample.data.GitHubRepositoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
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
    private val repo: GitHubRepositoryRepository,
    scope: CoroutineScope,
) : ViewModel(scope) {
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
                    repo.getRepository(query, "jeffmcnd")
                        .asResult()
                        .map(::toMainUiState)
                }
            }

    private fun toMainUiState(result: Result<GitHubRepository?>): MainUiState =
        when (result) {
            is Result.Loading -> MainUiState.Loading
            is Result.Success -> MainUiState.Success(getSuccessText(result.data))
            is Result.Error -> MainUiState.LoadFailed
        }

    private fun getSuccessText(repository: GitHubRepository?): String =
        if (repository != null) {
            repository.description ?: "No description specified."
        } else {
            ""
        }
}

private const val SEARCH_QUERY = "searchQuery"
private const val SEARCH_QUERY_MIN_LENGTH = 2