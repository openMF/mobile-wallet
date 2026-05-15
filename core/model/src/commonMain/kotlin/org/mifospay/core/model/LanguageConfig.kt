/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.core.model

import kotlinx.serialization.Serializable

@Serializable
enum class LanguageConfig(
    val localName: String?,
    val languageName: String,
) {
    DEFAULT(
        localName = null,
        languageName = "System (Default)",
    ),
    ENGLISH(
        localName = "en",
        languageName = "English (English)",
    ),
    FRENCH(
        localName = "fr",
        languageName = "French (Français)",
    ),
    SPANISH(
        localName = "es",
        languageName = "Spanish (Español)",
    ),
    GERMAN(
        localName = "de",
        languageName = "German (Deutsch)",
    ),
    PORTUGUESE(
        localName = "pt",
        languageName = "Portuguese (Português)",
    ),
    RUSSIAN(
        localName = "ru",
        languageName = "Russian (Русский)",
    ),
    HINDI(
        localName = "hi",
        languageName = "Hindi (हिन्दी)",
    ),
    CHINESE(
        localName = "zh",
        languageName = "Chinese (中文)",
    ),
    JAPANESE(
        localName = "ja",
        languageName = "Japanese (日本語)",
    ),
    TURKISH(
        localName = "tr",
        languageName = "Turkish (Türkçe)",
    ),
    ARABIC(
        localName = "ar",
        languageName = "Arabic (عربى)",
    ),
    URDU(
        localName = "ur",
        languageName = "Urdu (اُردُو)",
    ),
    BENGALI(
        localName = "bn",
        languageName = "Bengali (বাঙালি)",
    ),
    INDONESIAN(
        localName = "id",
        languageName = "Indonesian (bahasa Indonesia)",
    ),
    KHMER(
        localName = "km",
        languageName = "Khmer (ភាសាខ្មែរ)",
    ),
    KANNADA(
        localName = "kn",
        languageName = "Kannada (ಕನ್ನಡ)",
    ),
    TELUGU(
        localName = "te",
        languageName = "Telugu (తెలుగు)",
    ),
    BURMESE(
        localName = "my",
        languageName = "Burmese (မြန်မာ)",
    ),
    POLISH(
        localName = "pl",
        languageName = "Polish (Polski)",
    ),
    SWAHILI(
        localName = "sw",
        languageName = "Swahili (Kiswahili)",
    ),
    FARSI(
        localName = "fa",
        languageName = "Farsi (فارسی)",
    ),
}
