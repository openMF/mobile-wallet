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

    // Emits Unit whenever invalidate() is called (e.g. after a transfer).
    private val refreshTrigger = MutableSharedFlow<Unit>(extraBufferCapacity = 1)

    override fun invalidate() {
        refreshTrigger.tryEmit(Unit)
    }

    // Re-fetches balance when token, clientId, defaultAccount, OR a manual refresh changes.
    override val widgetStateFlow: Flow<WidgetState> = combine(
        userPreferencesRepository.token,
        userPreferencesRepository.clientId,
        userPreferencesRepository.defaultAccount,
        refreshTrigger.onStart { emit(Unit) }, // seed so combine starts immediately
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
