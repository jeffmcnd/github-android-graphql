package com.justjeff.graphqlexample

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.justjeff.graphqlexample.core.Output
import com.justjeff.graphqlexample.data.model.GitHubRepositoryParams
import com.justjeff.graphqlexample.data.repo.GitHubRepositoryRepository
import com.justjeff.graphqlexample.data.model.GitHubRepositoryResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filterNotNull
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

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    private fun mainUiState(): Flow<MainUiState> =
        searchQuery
            .debounce(500)
            .flatMapLatest { query ->
                if (query.length < SEARCH_QUERY_MIN_LENGTH) {
                    flowOf(MainUiState.EmptyQuery)
                } else {
                    repo.getRepository(GitHubRepositoryParams(query, "jeffmcnd"))
                        .map(::toMainUiState)
                        .filterNotNull()
                }
            }

    private fun toMainUiState(result: Output<GitHubRepositoryResult>): MainUiState? =
        when (result) {
            is Output.Loading -> MainUiState.Loading
            is Output.Success -> MainUiState.Success(getSuccessText(result.data))
            is Output.Error -> MainUiState.LoadFailed
            is Output.NothingNew -> null
        }

    private fun getSuccessText(result: GitHubRepositoryResult?): String {
        val repo = result?.repository
        return if (repo != null) {
            repo.description ?: "No description specified."
        } else {
            ""
        }
    }
}

private const val SEARCH_QUERY = "searchQuery"
private const val SEARCH_QUERY_MIN_LENGTH = 2