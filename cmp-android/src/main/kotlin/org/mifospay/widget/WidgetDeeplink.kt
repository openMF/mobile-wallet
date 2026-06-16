package org.mifospay.widget


/**
 * Single source of truth for all widget deep-link URIs.
 *
 * Referenced by:
 *   • [org.mifospay.widget.ui.WidgetAction] — fires the Intent
 *   • Your NavHost composable — registers navDeepLink { uriPattern = ... }
 *   • AndroidManifest.xml intent-filter — scheme + host only (no query)
 *
 * Lives in :cmp-shared so the NavHost (which is also in cmp-shared or
 * cmp-android) can import it without a circular dependency.
 */
object WidgetDeepLink {
    const val SCHEME = "mifospay"

    const val URI_ADD_INCOME = "$SCHEME://add"
    const val URI_ADD_EXPENSE = "$SCHEME://expense"
    const val URI_DASHBOARD = "$SCHEME://dashboard"
}