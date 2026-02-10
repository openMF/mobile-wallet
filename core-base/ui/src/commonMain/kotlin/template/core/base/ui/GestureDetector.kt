/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package template.core.base.ui

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

/**
 * Modifier that detects a multi-tap gesture within a configurable timeout window.
 *
 * @param tapCount Number of taps required to trigger the gesture. Defaults to 5.
 * @param tapTimeoutMs Time window in milliseconds. Defaults to 1000ms.
 * @param onGestureDetected Callback invoked when the gesture is detected.
 */
@OptIn(ExperimentalTime::class)
@Composable
fun Modifier.detectMultiTapGesture(
    tapCount: Int = 5,
    tapTimeoutMs: Long = 1000L,
    onGestureDetected: () -> Unit,
): Modifier {
    var currentTapCount by remember { mutableIntStateOf(0) }
    var lastTapTime by remember { mutableLongStateOf(0L) }

    return this.pointerInput(Unit) {
        detectTapGestures(
            onTap = {
                val currentTime = Clock.System.now().toEpochMilliseconds()
                if (currentTime - lastTapTime > tapTimeoutMs) {
                    currentTapCount = 0
                }
                currentTapCount++
                lastTapTime = currentTime

                if (currentTapCount >= tapCount) {
                    onGestureDetected()
                    currentTapCount = 0
                }
            },
        )
    }
}

/**
 * Modifier that detects a long press gesture.
 */
@Composable
fun Modifier.detectLongPressGesture(
    onLongPress: () -> Unit,
): Modifier {
    return this.pointerInput(Unit) {
        detectTapGestures(
            onLongPress = { onLongPress() },
        )
    }
}

/**
 * Modifier that detects a double tap gesture.
 */
@Composable
fun Modifier.detectDoubleTapGesture(
    onDoubleTap: () -> Unit,
): Modifier {
    return this.pointerInput(Unit) {
        detectTapGestures(
            onDoubleTap = { onDoubleTap() },
        )
    }
}
