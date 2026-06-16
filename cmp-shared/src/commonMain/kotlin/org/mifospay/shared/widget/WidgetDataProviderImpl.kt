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
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onStart
import org.mifospay.core.data.repository.WidgetRepository
import org.mifospay.core.datastore.UserPreferencesRepository

class WidgetDataProviderImpl(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val widgetRepository: WidgetRepository,
) : WidgetDataProvider {

    private val refreshTrigger = MutableSharedFlow<Unit>(extraBufferCapacity = 1)

    override fun invalidate() {
        refreshTrigger.tryEmit(Unit)
    }

    override val widgetStateFlow: Flow<WidgetState> = combine(
        userPreferencesRepository.token,
        userPreferencesRepository.clientId,
        userPreferencesRepository.defaultAccount,
        refreshTrigger.onStart { emit(Unit) },
    ) { token, clientId, defaultAccount, _ ->
        if (token.isNullOrBlank() || clientId == null || defaultAccount == null) {
            WidgetState.Unauthenticated
        } else {
            runCatching { widgetRepository.getWidgetData() }
                .fold(
                    onSuccess = { WidgetState.Authenticated(data = it) },
                    onFailure = { WidgetState.Error },
                )
        }
    }
}
