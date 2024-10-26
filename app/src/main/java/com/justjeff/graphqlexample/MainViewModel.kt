package com.justjeff.graphqlexample

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo.ApolloClient
import com.justjeff.graphqlexample.models.RepositoryQuery
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val client: ApolloClient) : ViewModel() {
    private val _state = MutableStateFlow(MainUiState("Loading..."))
    val state: StateFlow<MainUiState>
        get() = _state.asStateFlow()

    private val isInitialized = AtomicBoolean(false)

    fun initialize() {
        if (isInitialized.get()) {
            return
        }
        isInitialized.set(true)
        fetchRepositoryDesc()
    }

    private fun fetchRepositoryDesc() {
        viewModelScope.launch {
            val query = RepositoryQuery("github-react", "jeffmcnd")
            val response = client.query(query).execute()
            val data = response.data
            val error = response.errors?.firstOrNull()
            val text = when {
                data != null -> data.repository?.description ?: "No description specified."
                error != null -> error.message
                else -> "Unexpected error. Try again."
            }
            _state.update { it.copy(text = text) }
        }
    }
}