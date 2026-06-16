package org.mifospay.widget.ui

import org.mifospay.widget.WidgetDeepLink

/**
 * Updated to reference [WidgetDeepLink] constants.
 * URIs now have a single definition — change them in WidgetDeepLink only.
 */
enum class WidgetAction(val uri: String) {
    ADD_INCOME(WidgetDeepLink.URI_ADD_INCOME),
    ADD_EXPENSE(WidgetDeepLink.URI_ADD_EXPENSE),
    OPEN_DASHBOARD(WidgetDeepLink.URI_DASHBOARD),
}