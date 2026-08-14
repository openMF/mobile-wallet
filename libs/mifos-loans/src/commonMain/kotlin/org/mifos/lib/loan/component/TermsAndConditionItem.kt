/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifos.lib.loan.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import template.core.base.designsystem.theme.KptTheme

/**
 * Displays a single terms and conditions entry with a highlighted title and body text.
 *
 * @param title The resource ID for the term's heading.
 * @param description The resource ID for the detailed explanation of the term.
 */
@Composable
fun TermsAndConditionItem(
    title: StringResource,
    description: StringResource,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(KptTheme.spacing.sm),
    ) {
        Text(
            text = stringResource(title),
            style = KptTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
        )

        Text(
            text = stringResource(description),
            style = KptTheme.typography.bodySmall,
        )
        Spacer(modifier = Modifier.height(KptTheme.spacing.sm))
    }
}
