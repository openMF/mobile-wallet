/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.shared.instance

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@Composable
fun Modifier.detectInstanceSelectorGesture(
    onGestureDetected: () -> Unit,
): Modifier {
    var tapCount by remember { mutableIntStateOf(0) }
    var lastTapTime by remember { mutableLongStateOf(0L) }
    val tapTimeoutMs = 1000L // Reset tap count after 1 second

    return this.pointerInput(Unit) {
        detectTapGestures(
            onTap = {
                val currentTime = Clock.System.now().toEpochMilliseconds()
                if (currentTime - lastTapTime > tapTimeoutMs) {
                    tapCount = 0
                }
                tapCount++
                lastTapTime = currentTime

                if (tapCount >= 5) {
                    onGestureDetected()
                    tapCount = 0
                }
            },
        )
    }
}
