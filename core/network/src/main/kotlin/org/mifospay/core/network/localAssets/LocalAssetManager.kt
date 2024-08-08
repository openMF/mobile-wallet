package org.mifospay.core.network.localAssets

import java.io.InputStream

fun interface LocalAssetManager {
    fun open(fileName: String): InputStream
}
