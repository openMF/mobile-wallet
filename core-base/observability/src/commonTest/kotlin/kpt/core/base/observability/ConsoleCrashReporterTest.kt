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

import kotlin.test.Test
import kotlin.test.assertFalse

class ConsoleCrashReporterTest {

    @Test
    fun `isConfigured is false — stdout reporter is dev-only`() {
        val reporter = ConsoleCrashReporter()
        assertFalse(reporter.isConfigured)
    }

    @Test
    fun `all methods complete without throwing on the no-op stdout reporter`() {
        val reporter = ConsoleCrashReporter()
        // Each call below printlns; the test asserts no exception is raised. If any throw,
        // kotlin.test fails the test automatically — explicit assertion not required.
        reporter.recordException(IllegalStateException("boom"), message = "context line")
        reporter.recordMessage("hello world", level = CrashSeverity.Warning)
        reporter.recordMessage("no level supplied uses Info default")
        reporter.setUser("u-42")
        reporter.setUser(null)
    }
}
