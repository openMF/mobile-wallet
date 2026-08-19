/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package kpt.core.base.store.infra

import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes

class DefaultValidatorTest {

    @Test
    fun isValid_beforeFirstFetch_returnsTrue() = runTest {
        val validator = DefaultValidator<String>(ttl = 5.minutes)
        assertTrue(validator.isValid("data"))
    }

    @Test
    fun isValid_afterMarkFresh_returnsTrue() = runTest {
        val validator = DefaultValidator<String>(ttl = 5.minutes)
        validator.markFresh()
        assertTrue(validator.isValid("data"))
    }

    @Test
    fun isValid_afterTtlExpires_returnsFalse() = runTest {
        val validator = DefaultValidator<String>(ttl = 1.milliseconds)
        validator.markFresh()
        // Spin-wait for 1ms to ensure TTL expires
        val start = kotlin.time.TimeSource.Monotonic.markNow()
        while (start.elapsedNow() < 2.milliseconds) {
            // busy wait
        }
        assertFalse(validator.isValid("data"))
    }

    @Test
    fun markFresh_resetsTimer() = runTest {
        val validator = DefaultValidator<String>(ttl = 1.milliseconds)
        validator.markFresh()
        val start = kotlin.time.TimeSource.Monotonic.markNow()
        while (start.elapsedNow() < 2.milliseconds) {
            // busy wait — let first TTL expire
        }
        assertFalse(validator.isValid("data"))
        // Mark fresh again
        validator.markFresh()
        assertTrue(validator.isValid("data"))
    }

    @Test
    fun withTtl_createsValidatorWithGivenDuration() = runTest {
        val validator = DefaultValidator.withTtl<String>(10.minutes)
        validator.markFresh()
        assertTrue(validator.isValid("data"))
    }

    @Test
    fun alwaysValid_alwaysReturnsTrue() = runTest {
        val validator = DefaultValidator.alwaysValid<String>()
        assertTrue(validator.isValid("data"))
    }

    @Test
    fun defaultTtl_is30Minutes() = runTest {
        val validator = DefaultValidator<String>()
        validator.markFresh()
        // Should be valid immediately (well within 30 min)
        assertTrue(validator.isValid("anything"))
    }
}
