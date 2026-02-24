/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.onboarding.language

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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import mobile_wallet.feature.onboarding_language.generated.resources.Res
import mobile_wallet.feature.onboarding_language.generated.resources.feature_onboarding_choose_your_app_language
import mobile_wallet.feature.onboarding_language.generated.resources.feature_onboarding_chosen_language_can_be_changed_later_in_the_settings
import mobile_wallet.feature.onboarding_language.generated.resources.feature_onboarding_submit
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import org.mifospay.core.designsystem.component.MifosButton
import org.mifospay.core.designsystem.component.MifosScaffold
import org.mifospay.core.designsystem.theme.MifosTheme
import org.mifospay.core.model.LanguageConfig
import org.mifospay.core.ui.utils.EventsEffect
import template.core.base.designsystem.theme.KptTheme

@Composable
fun OnboardingLanguageScreenRoute(
    onNavigateToNext: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: OnboardingLanguageViewModel = koinViewModel(),
) {
    val uiState by viewModel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewModel) { event ->
        when (event) {
            OnboardingLanguageEvent.NavigateToNext -> onNavigateToNext()
        }
    }

    OnboardingLanguageScreen(
        uiState = uiState,
        modifier = modifier,
        onAction = remember(viewModel) {
            { viewModel.trySendAction(it) }
        },
    )
}

@Composable
fun OnboardingLanguageScreen(
    uiState: OnboardingLanguageState,
    modifier: Modifier = Modifier,
    onAction: (OnboardingLanguageAction) -> Unit,
) {
    var selectedLanguage by rememberSaveable { mutableStateOf(uiState.currentLanguage) }

    MifosScaffold(
        modifier = modifier,
        containerColor = KptTheme.colorScheme.background,
        bottomBar = {
            MifosButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(KptTheme.spacing.md),
                onClick = { onAction(OnboardingLanguageAction.SetLanguage(selectedLanguage)) },
                text = { Text(text = stringResource(Res.string.feature_onboarding_submit)) },
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
            Spacer(modifier = Modifier.height(KptTheme.spacing.xl))

            Text(
                text = stringResource(Res.string.feature_onboarding_choose_your_app_language),
                style = KptTheme.typography.headlineMedium,
                color = KptTheme.colorScheme.onSurface,
            )

            Spacer(modifier = Modifier.height(KptTheme.spacing.md))

            Text(
                text = stringResource(Res.string.feature_onboarding_chosen_language_can_be_changed_later_in_the_settings),
                style = KptTheme.typography.bodySmall,
                color = KptTheme.colorScheme.secondary,
            )

            Spacer(modifier = Modifier.height(KptTheme.spacing.lg))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(KptTheme.spacing.sm),
            ) {
                items(LanguageConfig.entries) { language ->
                    LanguageSelectionItem(
                        language = language,
                        isSelected = selectedLanguage == language,
                        onSelect = { selectedLanguage = it },
                    )
                }
            }
        }
    }
}

@Composable
fun LanguageSelectionItem(
    language: LanguageConfig,
    isSelected: Boolean,
    onSelect: (LanguageConfig) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect(language) }
            .padding(vertical = KptTheme.spacing.sm),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RadioButton(
            selected = isSelected,
            onClick = { onSelect(language) },
        )
        Spacer(modifier = Modifier.width(KptTheme.spacing.sm))
        Text(
            text = language.languageName,
            style = KptTheme.typography.bodyLarge,
            color = KptTheme.colorScheme.onSurface,
        )
    }
}

@Preview
@Composable
private fun OnboardingLanguageScreenPreview() {
    MifosTheme {
        OnboardingLanguageScreen(
            uiState = OnboardingLanguageState(LanguageConfig.DEFAULT),
            onAction = {},
        )
    }
}
