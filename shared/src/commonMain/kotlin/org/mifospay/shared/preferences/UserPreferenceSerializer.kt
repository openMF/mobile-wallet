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

import androidx.datastore.core.okio.OkioSerializer
import okio.BufferedSink
import okio.BufferedSource
import okio.IOException
import org.mifospay.shared.commonMain.proto.UserPreferences

object UserPreferenceSerializer : OkioSerializer<UserPreferences> {
    override val defaultValue: UserPreferences
        get() = UserPreferences()

    override suspend fun readFrom(source: BufferedSource): UserPreferences {
        try {
            return UserPreferences.ADAPTER.decode(source)
        } catch (exception: IOException) {
            throw Exception(exception.message ?: "Serialization Exception")
        }
    }

    override suspend fun writeTo(t: UserPreferences, sink: BufferedSink) {
        sink.write(t.encode())
    }
}
