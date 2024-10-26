package com.justjeff.graphqlexample

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.network.okHttpClient
import com.justjeff.graphqlexample.models.RepositoryQuery
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import java.util.concurrent.atomic.AtomicBoolean

class MainViewModel(private val client: ApolloClient) : ViewModel() {
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

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val client = ApolloClient.Builder()
                    .serverUrl("https://api.github.com/graphql")
                    .okHttpClient(
                        OkHttpClient.Builder()
                            .addInterceptor(AuthorizationInterceptor())
                            .build()
                    )
                    .build()
                return MainViewModel(client) as T
            }
        }
    }
}