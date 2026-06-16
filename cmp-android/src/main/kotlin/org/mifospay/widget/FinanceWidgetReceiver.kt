package org.mifospay.widget

import android.content.Context
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.mifospay.core.data.repository.WidgetManagerRepository

class FinanceWidgetReceiver :
    GlanceAppWidgetReceiver(),
    KoinComponent {

    override val glanceAppWidget = FinanceGlanceWidget

    private val widgetManager: WidgetManagerRepository by inject()

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        widgetManager.schedule()
    }

    override fun onDisabled(context: Context) {
        super.onDisabled(context)
        widgetManager.cancel()
    }
}