package org.mifos.mobilewallet.mifospay.designsystem.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import org.mifos.mobilewallet.mifospay.designsystem.theme.mifosText
import org.mifos.mobilewallet.mifospay.designsystem.theme.styleMifosTopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MifosTopBar(
    topBarTitle: Int,
    backPress: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = stringResource(id = topBarTitle),
                style = styleMifosTopBar
            )
        },
        navigationIcon = {
            IconButton(onClick = { backPress.invoke() }) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = mifosText
                )
            }
        },
        colors = TopAppBarDefaults.mediumTopAppBarColors(containerColor = Color.White)
    )
}