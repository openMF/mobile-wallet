/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.shared.preferences

import androidx.datastore.core.DataStore
import okio.FileSystem
import okio.Path.Companion.toPath
import org.mifospay.shared.commonMain.proto.UserPreferences
import org.mifospay.shared.di.AndroidPlatformContextProvider

actual fun getDataStore(): DataStore<UserPreferences> {
    val content = requireNotNull(AndroidPlatformContextProvider.context)
    val producePath = { content.filesDir.resolve(DATA_STORE_FILE_NAME).absolutePath.toPath() }

    return createDataStore(fileSystem = FileSystem.SYSTEM, producePath = producePath)
}
