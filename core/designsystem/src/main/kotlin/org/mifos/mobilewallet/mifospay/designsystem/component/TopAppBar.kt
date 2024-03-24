
@file:OptIn(ExperimentalMaterial3Api::class)

package org.mifos.mobilewallet.mifospay.designsystem.component

import androidx.annotation.StringRes
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import org.mifos.mobilewallet.mifospay.designsystem.icon.MifosIcons
import org.mifos.mobilewallet.mifospay.designsystem.theme.MifosTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MifosTopAppBar(
    modifier: Modifier = Modifier,
    @StringRes titleRes: Int,
    navigationIcon: ImageVector? = null,
    navigationIconContentDescription: String? = null,
    actionIcon: ImageVector,
    actionIconContentDescription: String,
    colors: TopAppBarColors = TopAppBarDefaults.centerAlignedTopAppBarColors(),
    onNavigationClick: (() -> Unit)? = null,
    onActionClick: () -> Unit = {},
) {
    CenterAlignedTopAppBar(
        title = { Text(text = stringResource(id = titleRes)) },
        navigationIcon = {
            navigationIcon?.let {
                IconButton(onClick = onNavigationClick!!) {
                    Icon(
                        imageVector = navigationIcon,
                        contentDescription = navigationIconContentDescription,
                        tint = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }
        },
        actions = {
            IconButton(onClick = onActionClick) {
                Icon(
                    imageVector = actionIcon,
                    contentDescription = actionIconContentDescription,
                    tint = MaterialTheme.colorScheme.onSurface,
                )
            }
        },
        colors = colors,
        modifier = modifier.testTag("mifosTopAppBar"),
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview("Top App Bar")
@Composable
private fun MifosTopAppBarPreview() {
    MifosTheme {
        MifosTopAppBar(
            titleRes = android.R.string.untitled,
            navigationIcon = MifosIcons.Search,
            navigationIconContentDescription = "Navigation icon",
            actionIcon = MifosIcons.MoreVert,
            actionIconContentDescription = "Action icon",
        )
    }
}
