package org.mifospay.widget.ui

import android.content.Context
import android.content.Intent
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import androidx.core.net.toUri

/**
 * Glance [ActionCallback] implementations.
 * Only imports: Glance + :core:model. Zero domain/data imports.
 */

class AddIncomeAction : ActionCallback {
    override suspend fun onAction(context: Context, glanceId: GlanceId, parameters: ActionParameters) {
        context.launchWidgetAction(WidgetAction.ADD_INCOME)
    }
}

class AddExpenseAction : ActionCallback {
    override suspend fun onAction(context: Context, glanceId: GlanceId, parameters: ActionParameters) {
        context.launchWidgetAction(WidgetAction.ADD_EXPENSE)
    }
}

class OpenDashboardAction : ActionCallback {
    override suspend fun onAction(context: Context, glanceId: GlanceId, parameters: ActionParameters) {
        context.launchWidgetAction(WidgetAction.OPEN_DASHBOARD)
    }
}

private fun Context.launchWidgetAction(action: WidgetAction) {
    startActivity(
        Intent(Intent.ACTION_VIEW, action.uri.toUri()).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_SINGLE_TOP
            setPackage(packageName)
        },
    )
}