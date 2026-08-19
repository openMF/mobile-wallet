/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package kpt.core.base.platform.intent

import androidx.compose.ui.graphics.ImageBitmap
import com.mobilebytelabs.kmptoolkit.intentlauncher.ExperimentalIntentLauncherApi
import com.mobilebytelabs.kmptoolkit.intentlauncher.IntentResult
import com.mobilebytelabs.kmptoolkit.intentlauncher.SystemIntents
import com.mobilebytelabs.kmptoolkit.share.ExperimentalShareApi
import com.mobilebytelabs.kmptoolkit.share.Share
import com.mobilebytelabs.kmptoolkit.share.SharePayload
import com.mobilebytelabs.kmptoolkit.share.file
import com.mobilebytelabs.kmptoolkit.share.text
import com.mobilebytelabs.kmptoolkit.share.url
import kpt.core.base.platform.model.MimeType

/**
 * Single-source-set IntentManager implementation. Delegates every method to KmpToolkit's
 * `cmp-share` (share sheets) and `cmp-intent-launcher` (system intents).
 *
 * Both toolkit modules handle Android `Context` bootstrapping via auto-init
 * `ContentProvider`s — no constructor argument needed on Android (or any other target).
 *
 * Per-platform image encoding (PNG via Skia on non-Android, native Bitmap on Android)
 * is delegated to cmp-share through the [shareImage] path — this file does not touch
 * any platform-specific image API.
 */
@OptIn(ExperimentalShareApi::class, ExperimentalIntentLauncherApi::class)
class IntentManagerImpl : IntentManager {

    override suspend fun launchUri(uri: String) {
        Share.url(uri)
    }

    override suspend fun shareText(text: String) {
        Share.text(text)
    }

    override suspend fun shareFile(fileUri: String, mimeType: MimeType) {
        Share.file(fileUri, mimeType.value)
    }

    override suspend fun shareFile(fileUri: String, mimeType: MimeType, extraText: String) {
        Share.share(
            SharePayload.Multi(
                items = listOf(
                    SharePayload.Text(extraText),
                    SharePayload.File(fileUri, mimeType.value),
                ),
            ),
        )
    }

    override suspend fun shareImage(title: String, image: ImageBitmap) {
        val bytes = encodeImageAsPng(image) ?: return
        Share.share(
            SharePayload.Image(
                bytes = bytes,
                mimeType = "image/png",
                filename = "$title.png",
            ),
        )
    }

    override suspend fun openAppSettings(): IntentResult = SystemIntents.openAppSettings()

    override suspend fun createDocument(fileName: String, mimeType: String): IntentResult =
        SystemIntents.createDocument(fileName, mimeType)
}

/**
 * Per-platform PNG encoding of an [ImageBitmap]. Android uses the native `Bitmap.compress`
 * path (no Skia dep on Android); every other Compose-MP target uses Skia.
 */
internal expect fun encodeImageAsPng(image: ImageBitmap): ByteArray?
