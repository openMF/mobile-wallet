/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.core.network

import androidx.annotation.VisibleForTesting
import org.mifospay.core.network.localAssets.LocalAssetManager
import java.io.File
import java.io.InputStream
import java.util.Properties

/**
 * This class helps with loading Android `/assets` files, especially when running JVM unit tests.
 * It must remain on the root package for an easier [Class.getResource] with relative paths.
 * @see https://developer.android.com/reference/tools/gradle-api/7.3/com/android/build/api/dsl/UnitTestOptions
 */
@VisibleForTesting
internal object JvmLocalAssetManager : LocalAssetManager {
    private val config =
        requireNotNull(javaClass.getResource("com/android/tools/test_config.properties")) {
            """
            Missing Android resources properties file.
            Did you forget to enable the feature in the gradle build file?
            android.testOptions.unitTests.isIncludeAndroidResources = true
            """.trimIndent()
        }
    private val properties = Properties().apply { config.openStream().use(::load) }
    private val assets = File(properties["android_merged_assets"].toString())

    override fun open(fileName: String): InputStream = File(assets, fileName).inputStream()
}
