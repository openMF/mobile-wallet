package org.mifospay.core.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.mifospay.core.designsystem.icon.MifosIcons
import template.core.base.designsystem.theme.KptTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MifosSearchBar(
    query: String,
    placeHolder: String,
    showClearButton: Boolean = true,
    onQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    onClearQuery: () -> Unit = {},
    onClick: () -> Unit = {},
    enabled:Boolean=true,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = KptTheme.spacing.md)
            .clickable(onClick = onClick)
    ) {
        SearchBar(
            inputField = {
                SearchBarDefaults.InputField(
                    query = query,
                    onQueryChange = onQueryChange,
                    onSearch = onSearch,
                    expanded = false,
                    onExpandedChange = {},
                    enabled = enabled,
                    placeholder = { Text(text = placeHolder) },
                    leadingIcon = {
                        Icon(
                            imageVector = MifosIcons.Search,
                            contentDescription = null
                        )
                    },
                    trailingIcon = {
                        if (showClearButton && query.isNotEmpty()) {
                            IconButton(onClick = onClearQuery) {
                                Icon(
                                    imageVector = MifosIcons.Close,
                                    contentDescription = null
                                )
                            }
                        }
                    },
                    interactionSource = null
                )
            },
            expanded = false,
            onExpandedChange = {},
            modifier = Modifier.fillMaxWidth(),
            shape = SearchBarDefaults.inputFieldShape,
            colors = SearchBarDefaults.colors(),
            tonalElevation = SearchBarDefaults.TonalElevation,
            shadowElevation = SearchBarDefaults.ShadowElevation,
            windowInsets = SearchBarDefaults.windowInsets,
            content = {}
        )
    }
}
