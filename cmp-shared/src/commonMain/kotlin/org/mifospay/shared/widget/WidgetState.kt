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

import org.mifospay.core.model.widget.WidgetData

sealed interface WidgetState {

    /** User is not logged in — widget shows a "Sign in" prompt. */
    data object Unauthenticated : WidgetState

    /** User is logged in — widget renders balance, budget, quick actions. */
    data class Authenticated(val data: WidgetData) : WidgetState

    /** Balance fetch failed — widget shows an error prompt. */
    data object Error : WidgetState
}
