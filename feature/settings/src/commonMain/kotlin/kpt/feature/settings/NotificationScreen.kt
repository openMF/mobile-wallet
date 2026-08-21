/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package kpt.feature.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import kpt.core.base.designsystem.component.HeroCard
import kpt.core.designsystem.icon.AppIcons
import kpt.core.designsystem.theme.spacing
import kpt.core.ui.scaffold.KptScaffold
import kpt.feature.settings.generated.resources.Res
import kpt.feature.settings.generated.resources.feature_settings_notifications_empty_message
import kpt.feature.settings.generated.resources.feature_settings_notifications_empty_title
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun NotificationScreen(modifier: Modifier = Modifier, onBackClick: () -> Unit) {
    NotificationScreenContent(
        modifier = modifier.testTag(TestTags.Notification.SCREEN),
        onBackClick = onBackClick,
    )
}

@Composable
internal fun NotificationScreenContent(modifier: Modifier = Modifier, onBackClick: () -> Unit) {
    val sp = MaterialTheme.spacing
    KptScaffold(
        onNavigationIconClick = onBackClick,
        title = "Notification",
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(sp.lg),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            HeroCard {
                Column(
                    modifier = Modifier.padding(sp.md),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(sp.sm),
                ) {
                    Icon(
                        imageVector = AppIcons.OutlinedNotifications,
                        contentDescription = null,
                        modifier = Modifier.padding(sp.sm),
                    )
                    Text(
                        text = stringResource(Res.string.feature_settings_notifications_empty_title),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(
                        text = stringResource(Res.string.feature_settings_notifications_empty_message),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}
