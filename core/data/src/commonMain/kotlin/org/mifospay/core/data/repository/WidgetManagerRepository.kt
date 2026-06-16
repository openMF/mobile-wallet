package org.mifospay.core.data.repository

import org.mifospay.core.model.widget.WidgetData

interface WidgetManagerRepository {
    suspend fun refresh()

    suspend fun update(data: WidgetData)

    suspend fun clear()

    fun schedule()

    fun cancel()
}