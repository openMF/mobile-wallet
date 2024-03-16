package org.mifos.mobilewallet.mifospay.home.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import org.mifos.mobilewallet.mifospay.R
import org.mifos.mobilewallet.mifospay.home.presenter.ProfileUiState
import org.mifos.mobilewallet.mifospay.ui.ProfileImage

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProfileScreen(
    profileState: ProfileUiState,
    onEditProfile: () -> Unit,
    onSettings: () -> Unit,
) {
    var showDetails by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        when (profileState) {
            ProfileUiState.Loading -> {}
            is ProfileUiState.Success -> {
                ProfileImage(bitmap = profileState.bitmapImage)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp, bottom = 8.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = profileState.name.toString(),
                        style = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Medium),
                    )
                    IconButton(onClick = { showDetails = !showDetails }) {
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            tint = Color.Black,
                            contentDescription = null
                        )
                    }
                }

                if (showDetails) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        DetailItem(
                            label = stringResource(id = R.string.email),
                            value = profileState.email.toString()
                        )
                        DetailItem(
                            label = stringResource(id = R.string.vpa),
                            value = profileState.vpa.toString()
                        )
                        DetailItem(
                            label = stringResource(id = R.string.mobile),
                            value = profileState.mobile.toString()
                        )
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(start = 24.dp, end = 24.dp)
                ) {
                    FlowRow(
                        modifier = Modifier
                            .fillMaxWidth(),
                        maxItemsInEachRow = 2
                    ) {
                        ProfileItemCard(
                            modifier = Modifier
                                .padding(end = 8.dp, bottom = 8.dp)
                                .weight(1f),
                            icon = R.drawable.qrcode_black,
                            text = R.string.personal_qr_code,
                            onClick = {}
                        )

                        ProfileItemCard(
                            modifier = Modifier
                                .padding(start = 8.dp, bottom = 8.dp)
                                .weight(1f),
                            icon = R.drawable.ic_bank,
                            text = R.string.link_bank_account,
                            onClick = {}
                        )

                        ProfileItemCard(
                            modifier = Modifier
                                .padding(top = 8.dp, bottom = 8.dp),
                            icon = R.drawable.ic_contact,
                            text = R.string.edit_profile,
                            onClick = { onEditProfile.invoke() }
                        )

                        ProfileItemCard(
                            modifier = Modifier
                                .padding(top = 8.dp),
                            icon = R.drawable.ic_setting,
                            text = R.string.settings,
                            onClick = { onSettings.invoke() }
                        )
                    }
                }
            }
        }
    }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun ProfileScreenPreview() {
    ProfileScreen(hiltViewModel(), {}, {})
}