/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.mifospay.core.designsystem.component.MfLoadingWheel
import org.mifospay.core.designsystem.component.MifosButton
import org.mifospay.core.designsystem.icon.MifosIcons
import org.mifospay.core.designsystem.theme.MifosBlue
import org.mifospay.core.ui.ProfileImage

@Composable
fun ProfileRoute(
    onLinkAccount: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = hiltViewModel(),
) {
    val profileState by viewModel.profileState.collectAsStateWithLifecycle()

    ProfileScreenContent(
        profileState = profileState,
        onLinkAccount = onLinkAccount,
        modifier = modifier,
    )
}

@Composable
fun ProfileScreenContent(
    profileState: ProfileUiState,
    onLinkAccount: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        when (profileState) {
            is ProfileUiState.Loading -> MfLoadingWheel()

            is ProfileUiState.Success -> {
                ProfileImage(bitmap = profileState.bitmapImage)

                ProfileDetailsCard(
                    name = profileState.name ?: "",
                    email = profileState.email ?: "",
                    vpa = profileState.vpa ?: "",
                    mobile = profileState.mobile ?: "",
                )

                MifosButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .height(54.dp),
                    color = MifosBlue,
                    text = { Text(text = stringResource(id = R.string.feature_profile_personal_qr_code)) },
                    onClick = { /*TODO*/ },
                    leadingIcon = {
                        Icon(
                            imageVector = MifosIcons.QrCode,
                            contentDescription = "Personal QR Code",
                        )
                    },
                )

                Spacer(modifier = Modifier.height(20.dp))

                MifosButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .height(55.dp),
                    color = MifosBlue,
                    text = { Text(text = stringResource(id = R.string.feature_profile_link_bank_account)) },
                    onClick = onLinkAccount,
                    leadingIcon = {
                        Icon(imageVector = MifosIcons.AttachMoney, contentDescription = "")
                    },
                )

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

internal class ProfilePreviewProvider : PreviewParameterProvider<ProfileUiState> {
    override val values: Sequence<ProfileUiState>
        get() = sequenceOf(
            ProfileUiState.Loading,
            ProfileUiState.Success(
                name = "John Doe",
                email = "john.doe@example.com",
                vpa = "john@vpa",
                mobile = "+1234567890",
                bitmapImage = null,
            ),
        )
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
private fun ProfileScreenPreview(
    @PreviewParameter(ProfilePreviewProvider::class)
    profileState: ProfileUiState,
) {
    ProfileScreenContent(
        profileState = profileState,
        onLinkAccount = {},
    )
}
