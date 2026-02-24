/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.language

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import mobile_wallet.feature.settings.generated.resources.Res
import mobile_wallet.feature.settings.generated.resources.feature_settings_change_language
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.mifospay.core.designsystem.component.MifosButton
import org.mifospay.core.designsystem.component.MifosScaffold
import org.mifospay.core.designsystem.component.MifosTopBar
import org.mifospay.core.model.LanguageConfig
import org.mifospay.core.ui.utils.EventsEffect
import template.core.base.designsystem.theme.KptTheme

@Composable
fun LanguageScreenRoute(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LanguageViewModel = koinViewModel(),
) {
    val uiState by viewModel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewModel) { event ->
        when (event) {
            LanguageEvent.NavigateBack -> onNavigateBack()
        }
    }

    LanguageScreen(
        uiState = uiState,
        modifier = modifier,
        onAction = remember(viewModel) {
            { viewModel.trySendAction(it) }
        },
    )
}

@Composable
fun LanguageScreen(
    uiState: LanguageState,
    onAction: (LanguageAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    MifosScaffold(
        modifier = modifier,
        topBar = {
            MifosTopBar(
                topBarTitle = stringResource(Res.string.feature_settings_change_language),
                backPress = { onAction(LanguageAction.OnNavigateBack) },
            )
        },
        bottomBar = {
            MifosButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(KptTheme.spacing.md),
                onClick = { onAction(LanguageAction.SetLanguage(uiState.selectedLanguage)) },
                text = { Text(text = stringResource(Res.string.feature_settings_change_language)) },
            )
        },
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .padding(horizontal = KptTheme.spacing.md),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(KptTheme.spacing.sm),
            ) {
                items(LanguageConfig.entries) { language ->
                    LanguageItem(
                        language = language,
                        isSelected = language == uiState.selectedLanguage,
                        onLanguageSelected = { onAction(LanguageAction.LanguageSelected(language)) },
                    )
                }
            }
        }
    }
}

@Composable
fun LanguageItem(
    language: LanguageConfig,
    isSelected: Boolean,
    onLanguageSelected: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onLanguageSelected)
            .padding(vertical = KptTheme.spacing.sm),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RadioButton(
            selected = isSelected,
            onClick = onLanguageSelected,
        )
        Spacer(modifier = Modifier.width(KptTheme.spacing.sm))
        Text(
            text = language.languageName,
            style = KptTheme.typography.bodyLarge,
            color = KptTheme.colorScheme.onSurface,
        )
    }
}
