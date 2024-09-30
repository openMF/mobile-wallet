/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.core.network.localAssets

import okio.FileHandle
import okio.FileSystem
import okio.Path.Companion.toPath
import okio.SYSTEM

internal object JvmLocalAssetManager : LocalAssetManager {
    override fun open(fileName: String): FileHandle {
        val path = fileName.toPath()
        return FileSystem.SYSTEM.openReadOnly(path)
    }
}
