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
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.okio.OkioStorage
import okio.FileSystem
import okio.Path
import org.mifospay.shared.commonMain.proto.UserPreferences
internal const val DATA_STORE_FILE_NAME = "user.preferences_pb"

expect fun getDataStore(): DataStore<UserPreferences>

fun createDataStore(
    fileSystem: FileSystem,
    producePath: () -> Path,
): DataStore<UserPreferences> =
    DataStoreFactory.create(
        storage = OkioStorage(
            fileSystem = fileSystem,
            producePath = producePath,
            serializer = UserPreferenceSerializer,
        ),
    )
