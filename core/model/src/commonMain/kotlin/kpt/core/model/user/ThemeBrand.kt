/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package kpt.core.model.user

enum class ThemeBrand(val brandName: String) {
    DEFAULT("Default"),
    ANDROID("Android"),
    ;

    companion object {
        fun fromString(value: String): ThemeBrand {
            return entries.find { it.brandName.equals(value, ignoreCase = true) } ?: DEFAULT
        }
    }
}
