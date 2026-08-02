/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package kpt.core.base.observability

/**
 * Default [CrashReporter] binding that writes events to stdout.
 *
 * Suitable for local development and CI runs where you want to see crash-relevant events
 * in the build log without wiring a real crash-reporter SDK. **Not** suitable for shipped
 * builds — forks should override the binding with a real provider (Firebase Crashlytics,
 * Sentry, Bugsnag, etc.).
 *
 * Output shape:
 *
 * ```
 * [CrashReporter] Exception: java.io.IOException: connect timed out — message="retryAll: gave up"
 * [CrashReporter] Info: user signed in
 * [CrashReporter] User: u-12345 (or null on sign-out)
 * ```
 *
 * [isConfigured] is `false` — forks that gate "is observability set up?" startup checks on
 * `isConfigured` will see this provider as un-configured and can show appropriate dev-mode
 * UI (e.g. a banner that says "Crash reporting disabled — using stdout").
 */
class ConsoleCrashReporter : CrashReporter {

    override val isConfigured: Boolean = false

    override fun recordException(throwable: Throwable, message: String?) {
        val suffix = message?.let { " — message=\"$it\"" } ?: ""
        println("[CrashReporter] Exception: ${throwable::class.simpleName}: ${throwable.message}$suffix")
        // Print top of stack trace for local debugging
        throwable.stackTraceToString()
            .lineSequence()
            .take(10)
            .forEach { line -> println("[CrashReporter]   $line") }
    }

    override fun recordMessage(message: String, level: CrashSeverity) {
        println("[CrashReporter] $level: $message")
    }

    override fun setUser(userId: String?) {
        println("[CrashReporter] User: ${userId ?: "null"}")
    }
}
