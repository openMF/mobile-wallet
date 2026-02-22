/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.auth.chooseAuthOption.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import cmp.sample.shared.chooseAuthOption.DialogButton

@Composable
fun MessageDialogBox(
    onDismiss: () -> Unit,
    onButtonClick: () -> Unit,
    dialogMessage: String,
    confirmButtonText: String,
) {
    Dialog(
        onDismissRequest = {
            onDismiss()
        },
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(White)
                .padding(16.dp),
        ) {
            Column {
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    dialogMessage,
                    modifier = Modifier
                        .padding(8.dp),
                    fontSize = 12.sp,
                )

                Spacer(modifier = Modifier.height(12.dp))
                DialogButton(
                    onClick = {
                        onButtonClick
                    },
                    modifier = Modifier
                        .padding(end = 8.dp),
                    text = confirmButtonText,
                )
            }
        }
    }
}
