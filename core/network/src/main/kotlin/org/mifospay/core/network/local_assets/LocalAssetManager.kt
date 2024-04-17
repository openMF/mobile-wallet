package org.mifospay.core.network.local_assets

import java.io.InputStream

fun interface LocalAssetManager {
    fun open(fileName: String): InputStream
}
