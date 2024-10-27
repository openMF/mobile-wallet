/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.kyc

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import mobile_wallet.feature.kyc.generated.resources.Res
import mobile_wallet.feature.kyc.generated.resources.feature_kyc_check
import mobile_wallet.feature.kyc.generated.resources.feature_kyc_complete_kyc
import mobile_wallet.feature.kyc.generated.resources.feature_kyc_error_oops
import mobile_wallet.feature.kyc.generated.resources.feature_kyc_loading
import mobile_wallet.feature.kyc.generated.resources.feature_kyc_unexpected_error_subtitle
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.mifospay.core.designsystem.component.MifosOverlayLoadingWheel
import org.mifospay.core.designsystem.icon.MifosIcons
import org.mifospay.core.ui.AvatarBox
import org.mifospay.core.ui.EmptyContentScreen
import org.mifospay.core.ui.utils.EventsEffect

@Composable
fun KYCScreen(
    onLevel1Clicked: () -> Unit,
    onLevel2Clicked: () -> Unit,
    onLevel3Clicked: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: KYCDescriptionViewModel = koinViewModel(),
) {
    val state by viewModel.kycState.collectAsStateWithLifecycle()

    EventsEffect(viewModel) { event ->
        when (event) {
            KycEvent.OnLevel1Clicked -> onLevel1Clicked.invoke()
            KycEvent.OnLevel2Clicked -> onLevel2Clicked.invoke()
            KycEvent.OnLevel3Clicked -> onLevel3Clicked.invoke()
        }
    }

    KYCDescriptionScreen(
        kUiState = state,
        onAction = remember(viewModel) {
            { viewModel.trySendAction(it) }
        },
        modifier = modifier,
    )
}

@Composable
private fun KYCDescriptionScreen(
    kUiState: KYCDescriptionUiState,
    onAction: (KycAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        when (kUiState) {
            KYCDescriptionUiState.Loading -> {
                MifosOverlayLoadingWheel(contentDesc = stringResource(Res.string.feature_kyc_loading))
            }

            is KYCDescriptionUiState.Error -> {
                EmptyContentScreen(
                    title = stringResource(Res.string.feature_kyc_error_oops),
                    subTitle = stringResource(Res.string.feature_kyc_unexpected_error_subtitle),
                    modifier = Modifier,
                    iconTint = MaterialTheme.colorScheme.primary,
                )
            }

            is KYCDescriptionUiState.Content -> {
                KYCDescriptionScreen(
                    currentLevel = kUiState.currentLevel,
                    onLevel1Clicked = { onAction(KycAction.Level1Clicked) },
                    onLevel2Clicked = { onAction(KycAction.Level2Clicked) },
                    onLevel3Clicked = { onAction(KycAction.Level3Clicked) },
                )
            }
        }
    }
}

@Composable
private fun KYCDescriptionScreen(
    currentLevel: KycLevel?,
    onLevel1Clicked: () -> Unit,
    onLevel2Clicked: () -> Unit,
    onLevel3Clicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = stringResource(Res.string.feature_kyc_complete_kyc),
            modifier = Modifier.padding(vertical = 20.dp),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleMedium,
        )

        KycLevel.entries.forEach { kycLevel ->
            KYCLevelCard(
                title = kycLevel.title,
                icon = kycLevel.icon,
                completed = (currentLevel?.level ?: -1) >= kycLevel.level,
                enabled = (currentLevel?.level ?: 0) >= kycLevel.level - 1,
                onClick = {
                    when (kycLevel.level) {
                        1 -> onLevel1Clicked.invoke()
                        2 -> onLevel2Clicked.invoke()
                        3 -> onLevel3Clicked.invoke()
                    }
                },
            )
        }
    }
}

@Composable
private fun KYCLevelCard(
    title: String,
    icon: ImageVector,
    completed: Boolean,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        OutlinedCard(
            enabled = enabled,
            onClick = onClick,
            shape = RoundedCornerShape(4.dp),
            colors = CardDefaults.outlinedCardColors(
                containerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
            ),
            border = CardDefaults.outlinedCardBorder(true),
            modifier = Modifier.weight(2.5f, false),
        ) {
            ListItem(
                headlineContent = {
                    Text(text = title)
                },
                leadingContent = {
                    AvatarBox(icon = icon)
                },
                trailingContent = {
                    if (completed) {
                        Icon(
                            imageVector = MifosIcons.OutlinedDoneAll,
                            contentDescription = stringResource(Res.string.feature_kyc_check),
                            tint = MaterialTheme.colorScheme.primary,
                        )
                    }
                },
                colors = ListItemDefaults.colors(
                    containerColor = Color.Transparent,
                ),
            )
        }
    }
}
