package com.justjeff.graphqlexample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.justjeff.graphqlexample.ui.theme.GraphQLExampleTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GraphQLExampleTheme {
                SearchRoute(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun SearchRoute(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel,
) {
    val query by viewModel.searchQuery.collectAsState()
    val state by viewModel.state.collectAsState()
    SearchScreen(
        modifier = modifier,
        query = query,
        state = state,
        onSearchQueryChanged = viewModel::onSearchQueryChanged,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    query: String,
    state: MainUiState,
    onSearchQueryChanged: (String) -> Unit,
) {
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        SearchBar(
            query = query,
            onQueryChange = onSearchQueryChanged,
            onSearch = onSearchQueryChanged,
            active = true,
            onActiveChange = { },
            modifier = modifier.padding(innerPadding)
        ) {
            val text = when (state) {
                is MainUiState.Loading -> "Loading..."
                is MainUiState.EmptyQuery -> "Please enter a repository name."
                is MainUiState.LoadFailed -> "Failed to load. Please try again."
                is MainUiState.Success -> if (state.isEmpty()) {
                    "No results found for that name."
                } else {
                    state.text
                }
            }
            Text(text = text, modifier = Modifier.padding(16.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    GraphQLExampleTheme {
        SearchScreen(
            query = "Repository Name",
            state = MainUiState.Success("Repository's description."),
            onSearchQueryChanged = { },
        )
    }
}