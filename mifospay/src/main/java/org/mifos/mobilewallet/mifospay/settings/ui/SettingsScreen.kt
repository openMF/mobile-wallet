package org.mifos.mobilewallet.mifospay.settings.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.mifos.mobilewallet.mifospay.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    backPress: () -> Unit,
    disable: () -> Unit,
    logout: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.settings),
                        style = TextStyle(
                            color = Color(0xFF212121),
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 20.sp
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { backPress.invoke() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color(0xFF212121)
                        )
                    }
                },
                colors = TopAppBarDefaults.mediumTopAppBarColors(containerColor = Color.White)
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(it)
        ) {
            Row(modifier = Modifier.padding(20.dp)) {
                MifosItemCard(
                    onClick = { /*TODO*/ },
                    colors = CardDefaults.cardColors(Color.White)
                ) {
                    Text(
                        text = stringResource(id = R.string.notification_settings),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        style = TextStyle(fontSize = 17.sp, color = Color(0xFF212121))
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {}

            Row(modifier = Modifier.padding(8.dp)) {
                MifosItemCard(
                    onClick = { disable.invoke() },
                    colors = CardDefaults.cardColors(Color.Black)
                ) {
                    Text(
                        text = stringResource(id = R.string.disable_account).uppercase(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        style = TextStyle(color = Color.White, textAlign = TextAlign.Center)
                    )
                }
            }

            Row(modifier = Modifier.padding(8.dp)) {
                MifosItemCard(
                    onClick = { logout.invoke() },
                    colors = CardDefaults.cardColors(Color.Black)
                ) {
                    Text(
                        text = stringResource(id = R.string.log_out).uppercase(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        style = TextStyle(color = Color.White, textAlign = TextAlign.Center)
                    )
                }
            }
        }
    }


}

@Composable
fun MifosItemCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(8.dp),
    elevation: Dp = 1.dp,
    onClick: () -> Unit,
    colors: CardColors = CardDefaults.cardColors(),
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        shape = shape,
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(
            defaultElevation = elevation
        ),
        colors = colors,
        content = content
    )
}

@Preview(showSystemUi = true)
@Composable
fun SettingsScreenPreview() {
    SettingsScreen({}, {}, {})
}