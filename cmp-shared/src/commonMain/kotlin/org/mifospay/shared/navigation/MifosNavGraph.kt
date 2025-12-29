/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.shared.navigation

import org.mifos.library.passcode.Intention

internal object MifosNavGraph {
    const val ROOT_GRAPH = "root_graph"
    const val LOGIN_GRAPH = "login_graph"
    const val MAIN_GRAPH = "main_graph"

    // Passcode graph with intention parameter
    private const val PASSCODE_GRAPH_BASE = "passcode_graph"
    private const val INTENTION_ARG = "intention"
    const val PASSCODE_GRAPH_ROUTE = "$PASSCODE_GRAPH_BASE?$INTENTION_ARG={$INTENTION_ARG}"

    fun passcodeGraphRoute(intention: Intention): String {
        return "$PASSCODE_GRAPH_BASE?$INTENTION_ARG=${intention.value}"
    }
}
