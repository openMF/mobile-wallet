/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
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
