package com.justjeff.graphqlexample

sealed class MainUiState {
    data object Loading : MainUiState()
    data object EmptyQuery : MainUiState()
    data object LoadFailed : MainUiState()
    data class Success(val text: String) : MainUiState() {
        fun isEmpty() = text.isBlank()
    }
}