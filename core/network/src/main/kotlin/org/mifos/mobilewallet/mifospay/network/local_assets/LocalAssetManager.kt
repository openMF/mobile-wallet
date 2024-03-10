package org.mifos.mobilewallet.mifospay.network.local_assets

import java.io.InputStream

fun interface LocalAssetManager {
    fun open(fileName: String): InputStream
}
