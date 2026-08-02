/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package kpt.core.model.user

/**
 * Represents the languages supported by the app.
 */
enum class LanguageConfig(
    val localeName: String?,
    val text: String,
) {
    DEFAULT(
        localeName = null,
        text = "System Default",
    ),
    AFRIKAANS(
        localeName = "af",
        text = "Afrikaans",
    ),
    BELARUSIAN(
        localeName = "be",
        text = "Беларуская",
    ),
    BULGARIAN(
        localeName = "bg",
        text = "български",
    ),
    CATALAN(
        localeName = "ca",
        text = "català",
    ),
    CZECH(
        localeName = "cs",
        text = "čeština",
    ),
    DANISH(
        localeName = "da",
        text = "Dansk",
    ),
    GERMAN(
        localeName = "de",
        text = "Deutsch",
    ),
    GREEK(
        localeName = "el",
        text = "Ελληνικά",
    ),
    ENGLISH(
        localeName = "en",
        text = "English",
    ),
    ENGLISH_BRITISH(
        localeName = "en-GB",
        text = "English (British)",
    ),
    SPANISH(
        localeName = "es",
        text = "Español",
    ),
    ESTONIAN(
        localeName = "et",
        text = "eesti",
    ),
    PERSIAN(
        localeName = "fa",
        text = "فارسی",
    ),
    FINNISH(
        localeName = "fi",
        text = "suomi",
    ),
    FRENCH(
        localeName = "fr",
        text = "Français",
    ),
    HINDI(
        localeName = "hi",
        text = "हिन्दी",
    ),
    CROATIAN(
        localeName = "hr",
        text = "hrvatski",
    ),
    HUNGARIAN(
        localeName = "hu",
        text = "magyar",
    ),
    INDONESIAN(
        localeName = "in",
        text = "Bahasa Indonesia",
    ),
    ITALIAN(
        localeName = "it",
        text = "Italiano",
    ),
    HEBREW(
        localeName = "iw",
        text = "עברית",
    ),
    JAPANESE(
        localeName = "ja",
        text = "日本語",
    ),
    KOREAN(
        localeName = "ko",
        text = "한국어",
    ),
    LATVIAN(
        localeName = "lv",
        text = "Latvietis",
    ),
    MALAYALAM(
        localeName = "ml",
        text = "മലയാളം",
    ),
    NORWEGIAN(
        localeName = "nb",
        text = "norsk (bokmål)",
    ),
    DUTCH(
        localeName = "nl",
        text = "Nederlands",
    ),
    POLISH(
        localeName = "pl",
        text = "Polski",
    ),
    PORTUGUESE_BRAZILIAN(
        localeName = "pt-BR",
        text = "Português do Brasil",
    ),
    PORTUGUESE(
        localeName = "pt-PT",
        text = "Português",
    ),
    ROMANIAN(
        localeName = "ro",
        text = "română",
    ),
    RUSSIAN(
        localeName = "ru",
        text = "русский",
    ),
    SLOVAK(
        localeName = "sk",
        text = "slovenčina",
    ),
    SWEDISH(
        localeName = "sv",
        text = "svenska",
    ),
    THAI(
        localeName = "th",
        text = "ไทย",
    ),
    TURKISH(
        localeName = "tr",
        text = "Türkçe",
    ),
    UKRAINIAN(
        localeName = "uk",
        text = "українська",
    ),
    VIETNAMESE(
        localeName = "vi",
        text = "Tiếng Việt",
    ),
    CHINESE_SIMPLIFIED(
        localeName = "zh-CN",
        text = "中文（中国大陆）",
    ),
    CHINESE_TRADITIONAL(
        localeName = "zh-TW",
        text = "中文（台灣）",
    ),
}
