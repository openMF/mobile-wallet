/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.core.model.user

import kotlinx.serialization.Serializable

@Serializable
data class Language(
    val localeName: String?,
    val text: String,
) {
    companion object {
        val DEFAULT = LanguageConfig.DEFAULT.toLanguage()
    }
}

fun LanguageConfig.toLanguage(): Language {
    return Language(
        localeName = this.localeName,
        text = this.text,
    )
}
