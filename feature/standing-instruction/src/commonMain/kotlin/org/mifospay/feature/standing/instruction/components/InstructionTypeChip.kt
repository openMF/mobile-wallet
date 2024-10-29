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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.mifospay.core.model.standinginstruction.StandingInstruction

@Composable
fun InstructionTypeChip(
    type: StandingInstruction.Option,
    modifier: Modifier = Modifier,
) {
    val (backgroundColor, contentColor) = when (type.id) {
        1L -> Color(0xFFE3F2FD) to Color(0xFF1565C0)
        2L -> Color(0xFFF3E5F5) to Color(0xFF7B1FA2)
        else -> Color(0xFFF5F5F5) to Color(0xFF616161)
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
