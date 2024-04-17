package org.mifospay.core.ui

import androidx.compose.runtime.compositionLocalOf
import kotlinx.datetime.TimeZone

/**
 * TimeZone that can be provided with the TimeZoneMonitor.
 * This way, it's not needed to pass every single composable the time zone to show in UI.
 */
val LocalTimeZone = compositionLocalOf { TimeZone.currentSystemDefault() }
