/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.standing.instruction.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.mifospay.core.designsystem.theme.SIChipDefaultPriorityBackground
import org.mifospay.core.designsystem.theme.SIChipDefaultPriorityContent
import org.mifospay.core.designsystem.theme.SIChipHighPriorityBackground
import org.mifospay.core.designsystem.theme.SIChipHighPriorityContent
import org.mifospay.core.designsystem.theme.SIChipLowPriorityBackground
import org.mifospay.core.designsystem.theme.SIChipLowPriorityContent
import org.mifospay.core.model.standinginstruction.StandingInstruction

@Composable
fun InstructionTypeChip(
    type: StandingInstruction.Option,
    modifier: Modifier = Modifier,
) {
    val (backgroundColor, contentColor) = when (type.id) {
        1L -> MaterialTheme.colorScheme.SIChipHighPriorityBackground to MaterialTheme.colorScheme.SIChipHighPriorityContent
        2L -> MaterialTheme.colorScheme.SIChipLowPriorityBackground to MaterialTheme.colorScheme.SIChipLowPriorityContent
        else -> MaterialTheme.colorScheme.SIChipDefaultPriorityBackground to MaterialTheme.colorScheme.SIChipDefaultPriorityContent
    }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(4.dp),
        color = backgroundColor,
        contentColor = contentColor,
    ) {
        Text(
            text = type.value,
            modifier = Modifier.padding(4.dp),
            style = MaterialTheme.typography.bodySmall,
        )
    }
}
