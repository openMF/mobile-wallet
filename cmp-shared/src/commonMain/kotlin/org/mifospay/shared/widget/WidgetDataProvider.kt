/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.shared.widget

import kotlinx.coroutines.flow.Flow

interface WidgetDataProvider {

    /** Emits a fresh [WidgetState] whenever auth state or balance changes. */
    val widgetStateFlow: Flow<WidgetState>

    /** Call after a transaction to force a balance re-fetch. */
    fun invalidate()
}
