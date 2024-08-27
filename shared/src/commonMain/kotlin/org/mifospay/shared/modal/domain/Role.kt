/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.shared.modal.domain

import org.mifospay.shared.CommonParcelable
import org.mifospay.shared.CommonParcelize

@CommonParcelize
data class Role(
    var id: String? = null,
    var name: String? = null,
    var description: String? = null,
    val disabled: Boolean,
): CommonParcelable {
    companion object
}
