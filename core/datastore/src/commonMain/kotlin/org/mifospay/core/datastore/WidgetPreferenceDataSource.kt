/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
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

class WidgetPreferenceDataSource(
    private val settings: Settings,
    private val dispatcher: CoroutineDispatcher,
) {
    private val _widgetData = MutableStateFlow(
        settings.decodeValue(
            key = WIDGET_DATA_KEY,
            serializer = WidgetData.serializer(),
            defaultValue = settings.decodeValueOrNull(
                key = WIDGET_DATA_KEY,
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
        key = WIDGET_DATA_KEY,
        serializer = WidgetData.serializer(),
        value = data,
    )
}
