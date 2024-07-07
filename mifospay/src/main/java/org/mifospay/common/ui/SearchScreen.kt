package org.mifospay.common.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mifospay.core.model.domain.SearchResult
import org.mifospay.R
import org.mifospay.common.presenter.SearchResultState
import org.mifospay.common.presenter.SearchViewModel
import org.mifospay.core.designsystem.component.MfLoadingWheel
import org.mifospay.core.designsystem.icon.MifosIcons
import org.mifospay.core.designsystem.theme.MifosTheme

@Composable
fun SearchScreenRoute(
    viewModel: SearchViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val searchResultState by viewModel.searchResults.collectAsState()

    SearchScreen(
        onBackClick = onBackClick,
        searchQueryChanged = viewModel::onSearchQueryChanged,
        searchQuery,
        searchResultState
    )
}

@Composable
fun SearchScreen(
    onBackClick: () -> Unit,
    searchQueryChanged: (String) -> Unit = {},
    searchQuery: String = "",
    searchResultState: SearchResultState
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        SearchToolbar(
            onBackClick = onBackClick,
            onSearchQueryChanged = searchQueryChanged,
            searchQuery = searchQuery,
        )

        when (searchResultState) {
            SearchResultState.Idle -> {}
            SearchResultState.Loading -> {
                MfLoadingWheel(
                    contentDesc = stringResource(R.string.loading),
                    backgroundColor = MaterialTheme.colorScheme.surface
                )
            }

            is SearchResultState.Success -> {
                SearchResultList(
                    searchResults = searchResultState.results
                )
            }

            is SearchResultState.Error -> {
                Text(text = searchResultState.message,
                    color = MaterialTheme.colorScheme.onSurface)
            }
        }
    }
}

@Composable
private fun SearchToolbar(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
    onSearchQueryChanged: (String) -> Unit,
    searchQuery: String = "",
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth(),
    ) {
        IconButton(onClick = { onBackClick() }) {
            Icon(
                imageVector = MifosIcons.ArrowBack,
                contentDescription = R.string.navigateBack.toString(),
            )
        }
        SearchTextField(
            onSearchQueryChanged = onSearchQueryChanged,
            searchQuery = searchQuery
        )
    }
}

@Composable
fun SearchTextField(
    onSearchQueryChanged: (String) -> Unit,
    searchQuery: String
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val onSearchExplicitlyTriggered = {
        keyboardController?.hide()
    }

    TextField(
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
        ),
        leadingIcon = {
            Icon(
                imageVector = MifosIcons.Search,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface,
            )
        },
        trailingIcon = {
            if (searchQuery.isNotEmpty()) {
                IconButton(
                    onClick = {
                        onSearchQueryChanged("")
                    },
                ) {
                    Icon(
                        imageVector = MifosIcons.Cancel,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }
        },
        onValueChange = {
            if ("\n" !in it) onSearchQueryChanged(it)
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .onKeyEvent {
                if (it.key == Key.Enter) {
                    onSearchExplicitlyTriggered()
                    true
                } else {
                    false
                }
            },
        shape = RoundedCornerShape(32.dp),
        value = searchQuery,
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Search,
        ),
        keyboardActions = KeyboardActions(
            onSearch = {
                onSearchExplicitlyTriggered()
            },
        ),
        maxLines = 1,
        singleLine = true,
    )
}

@Composable
fun SearchResultList(
    searchResults: List<SearchResult>,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
    ) {
        items(searchResults) { searchResult ->
            SearchResultItem(searchResult = searchResult)
        }
    }
}

@Composable
fun SearchResultItem(
    searchResult: SearchResult
) {
    Text(
        text = searchResult.resultName,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        color = MaterialTheme.colorScheme.onSurface
    )
}

@Preview
@Composable
private fun SearchToolbarPreview() {
    MifosTheme {
        SearchToolbar(
            onBackClick = {},
            onSearchQueryChanged = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SearchScreenSuccessPreview() {
    MifosTheme {
        SearchScreen(
            searchResultState = SearchResultState.Success(
                mutableListOf(
                    SearchResult(1, "John Doe", "Client"),
                    SearchResult(2, "Jane Smith", "Client"),
                    SearchResult(3, "example@email.com", "Email"),
                    SearchResult(4, "555-1234", "Phone Number")
                )
            ),
            onBackClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SearchScreenLoadingPreview() {
    MifosTheme {
        SearchScreen(
            searchResultState = SearchResultState.Loading,
            onBackClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SearchScreenIdlePreview() {
    MifosTheme {
        SearchScreen(
            searchResultState = SearchResultState.Idle,
            onBackClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SearchScreenErrorPreview() {
    MifosTheme {
        SearchScreen(
            searchResultState = SearchResultState.Error("Error"),
            onBackClick = {}
        )
    }
}


