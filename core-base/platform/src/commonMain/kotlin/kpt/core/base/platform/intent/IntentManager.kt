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
import kpt.core.base.platform.model.MimeType

/**
 * Platform-neutral surface for share + system-intent operations.
 *
 * Backed by KmpToolkit's `cmp-share` (share sheets) + `cmp-intent-launcher` (system
 * intents) so the implementation lives entirely in `commonMain` — Android no longer
 * needs its own actual. Targets: Android, JVM Desktop, iOS, JS, wasmJs (every
 * Compose-MP target the template declares).
 *
 * **Methods are suspend**: share sheets + settings deep-links are inherently async on
 * iOS / JS / wasmJs; the suspend signature avoids fire-and-forget hacks at consumer
 * call sites. Wrap calls in `LaunchedEffect` or `rememberCoroutineScope().launch { }`.
 *
 * **Migration from the prior interface**:
 * | Prior method | Replacement |
 * |---|---|
 * | `fun startActivity(intent: Any)` | Removed. Use cmp-intent-launcher's `IntentLauncher` directly. |
 * | `fun createDocumentIntent(fileName): Any` | Removed. Use `IntentLauncher` with `PickDocument` contract. |
 * | `fun startApplicationDetailsSettingsActivity()` | [`openAppSettings`] (suspend, returns `IntentResult`). |
 */
@OptIn(ExperimentalIntentLauncherApi::class)
interface IntentManager {
    /**
     * Opens the given URI in the platform's default handler (browser, mailto:, deep
     * link, etc.). Implemented via cmp-share's URL share sheet — on Android this routes
     * through `ACTION_SEND` so the user picks the handler; on iOS / desktop it presents
     * the system share sheet.
     */
    suspend fun launchUri(uri: String)

    /** Shares plain text via the platform share sheet. */
    suspend fun shareText(text: String)

    /** Shares a file URI with the given MIME type. */
    suspend fun shareFile(fileUri: String, mimeType: MimeType)

    /** Shares a file URI with the given MIME type and accompanying text. */
    suspend fun shareFile(fileUri: String, mimeType: MimeType, extraText: String)

    /** Shares an in-memory image. PNG-encoded via Skia on non-Android targets. */
    suspend fun shareImage(title: String, image: ImageBitmap)

    /**
     * Opens the host application's settings page. Behaviour per-platform documented in
     * [com.mobilebytelabs.kmptoolkit.intentlauncher.SystemIntents.openAppSettings].
     * Returns `IntentResult.Failed(UnsupportedPlatform)` on JS / wasmJs / tvOS / watchOS.
     */
    suspend fun openAppSettings(): IntentResult

    /**
     * Presents an OS save dialog and returns the chosen destination URI in
     * `IntentResult.Ok(IntentData(uri = ...))`. Per-platform behaviour documented in
     * [com.mobilebytelabs.kmptoolkit.intentlauncher.SystemIntents.createDocument]
     * (Android proxy Activity → SAF, iOS `UIDocumentPickerViewController` export mode,
     * JVM `JFileChooser`, JS / wasmJs `showSaveFilePicker`).
     *
     * Returns `Cancelled` when the user dismisses, `Failed(UnsupportedPlatform)` on
     * platforms without a save-dialog surface (tvOS, watchOS, mingw).
     *
     * @param fileName suggested filename pre-filled in the picker
     * @param mimeType MIME used to filter / suggest extension (defaults to "&#42;/&#42;")
     */
    suspend fun createDocument(fileName: String, mimeType: String = "*/*"): IntentResult
}
