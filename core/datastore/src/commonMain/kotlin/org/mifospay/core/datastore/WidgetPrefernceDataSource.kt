@file:OptIn(ExperimentalSerializationApi::class, ExperimentalSettingsApi::class)

package org.mifospay.core.datastore

import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.Settings
import com.russhwolf.settings.serialization.decodeValue
import com.russhwolf.settings.serialization.decodeValueOrNull
import com.russhwolf.settings.serialization.encodeValue
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import org.mifospay.core.model.widget.WidgetData

private const val WIDGET_DATA_KEY = "widget_data"

/**
 * Persistence layer for home screen widget data.
 *
 * Follows the same pattern as [UserPreferencesDataSource]:
 *   - [encodeValue] / [decodeValue] for kotlinx.serialization integration
 *   - [MutableStateFlow] seeded from storage on construction
 *   - All writes run on [dispatcher] via [withContext]
 *   - Private [Settings] extension functions per stored type
 *
 * Lives in :core:datastore alongside [UserPreferencesDataSource].
 */
class WidgetPreferencesDataSource(
    private val settings: Settings,
    private val dispatcher: CoroutineDispatcher,
) {
    private val _widgetData = MutableStateFlow(
        settings.decodeValue(
            key = WIDGET_DATA_KEY,
            serializer   = WidgetData.serializer(),
            defaultValue = settings.decodeValueOrNull(
                key        = WIDGET_DATA_KEY,
                serializer = WidgetData.serializer(),
            ) ?: WidgetData.DEFAULT,
        ),
    )

    val widgetData: StateFlow<WidgetData> = _widgetData.asStateFlow()

    suspend fun updateWidgetData(data: WidgetData) {
        withContext(dispatcher) {
            settings.putWidgetData(data)
            _widgetData.value = data
        }
    }

    suspend fun clearWidgetData() {
        withContext(dispatcher) {
            settings.remove(WIDGET_DATA_KEY)
            _widgetData.value = WidgetData.DEFAULT
        }
    }
}

private fun Settings.putWidgetData(data: WidgetData) {
    encodeValue(
        key        = WIDGET_DATA_KEY,
        serializer = WidgetData.serializer(),
        value      = data,
    )
}