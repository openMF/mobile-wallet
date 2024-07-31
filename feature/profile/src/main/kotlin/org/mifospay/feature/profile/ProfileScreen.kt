package org.mifospay.feature.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.mifospay.core.designsystem.icon.MifosIcons
import org.mifospay.core.ui.ProfileImage


@Composable
fun ProfileRoute(
    viewModel: ProfileViewModel = hiltViewModel(),
    onEditProfile: () -> Unit,
    onSettings: () -> Unit,
) {
    val profileState by viewModel.profileState.collectAsStateWithLifecycle()
    ProfileScreenContent(
        profileState = profileState,
        onEditProfile = onEditProfile,
        onSettings = onSettings
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProfileScreenContent(
    profileState: ProfileUiState,
    onEditProfile: () -> Unit,
    onSettings: () -> Unit,
) {
    var showDetails by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
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
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    IconButton(onClick = { showDetails = !showDetails }) {
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            tint = MaterialTheme.colorScheme.onSurface,
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
                            label = stringResource(id = R.string.feature_profile_email),
                            value = profileState.email.toString()
                        )
                        DetailItem(
                            label = stringResource(id = R.string.feature_profile_vpa),
                            value = profileState.vpa.toString()
                        )
                        DetailItem(
                            label = stringResource(id = R.string.feature_profile_mobile),
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
                            icon = MifosIcons.QR,
                            text = R.string.feature_profile_personal_qr_code,
                            onClick = {}
                        )

                        ProfileItemCard(
                            modifier = Modifier
                                .padding(start = 8.dp, bottom = 8.dp)
                                .weight(1f),
                            icon = MifosIcons.Bank,
                            text = R.string.feature_profile_link_bank_account,
                            onClick = {}
                        )

                        ProfileItemCard(
                            modifier = Modifier
                                .padding(top = 8.dp, bottom = 8.dp),
                            icon = MifosIcons.Contact,
                            text = R.string.feature_profile_edit_profile,
                            onClick = { onEditProfile.invoke() }
                        )

                        ProfileItemCard(
                            modifier = Modifier
                                .padding(top = 8.dp),
                            icon = MifosIcons.Settings,
                            text = R.string.feature_profile_settings,
                            onClick = { onSettings.invoke() }
                        )
                    }
                }
            }
        }
    }
}

class ProfilePreviewProvider : PreviewParameterProvider<ProfileUiState> {
    override val values: Sequence<ProfileUiState>
        get() = sequenceOf(
            ProfileUiState.Loading,
            ProfileUiState.Success(
                name = "John Doe",
                email = "john.doe@example.com",
                vpa = "john@vpa",
                mobile = "+1234567890",
                bitmapImage = null
            )
        )
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun ProfileScreenPreview(
    @PreviewParameter(ProfilePreviewProvider::class) profileState: ProfileUiState
) {
    ProfileScreenContent(
        profileState = profileState,
        onEditProfile = {},
        onSettings = {}
    )
}