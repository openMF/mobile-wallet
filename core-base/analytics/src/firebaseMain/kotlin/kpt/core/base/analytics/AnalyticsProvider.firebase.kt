/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package kpt.core.base.analytics

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.analytics.analytics

actual fun provideAnalyticsHelper(): AnalyticsHelper = FirebaseAnalyticsHelper(Firebase.analytics)
