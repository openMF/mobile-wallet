/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package kpt.core.base.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import kpt.core.base.platform.context.AppContext
import kpt.core.base.platform.intent.IntentManagerImpl
import kpt.core.base.platform.review.AppReviewManagerImpl
import kpt.core.base.platform.update.AppUpdateManagerImpl

@Composable
actual fun LocalManagerProvider(
    context: AppContext,
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(
        LocalAppReviewManager provides AppReviewManagerImpl(),
        LocalIntentManager provides IntentManagerImpl(),
        LocalAppUpdateManager provides AppUpdateManagerImpl(),
    ) {
        content()
    }
}
