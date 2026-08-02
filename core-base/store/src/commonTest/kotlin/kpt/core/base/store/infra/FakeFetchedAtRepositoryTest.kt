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
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.time.Clock
import kotlin.time.Duration.Companion.minutes
import kotlin.time.ExperimentalTime

/**
 * Validates the test fake's contract — both so other tests can rely on it and so
 * the production [FetchedAtRepository] interface stays exercisable in commonTest
 * without needing to spin up Room.
 */
@OptIn(ExperimentalTime::class)
class FakeFetchedAtRepositoryTest {

    @Test
    fun read_unknownKey_returnsNull() = runTest {
        val repo = FakeFetchedAtRepository()
        assertNull(repo.read("never-written"))
    }

    @Test
    fun write_thenRead_roundTrips() = runTest {
        val repo = FakeFetchedAtRepository()
        val now = Clock.System.now()
        repo.write("k", now)
        assertEquals(now, repo.read("k"))
    }

    @Test
    fun write_overwritesPreviousValue() = runTest {
        val repo = FakeFetchedAtRepository()
        repo.write("k", Clock.System.now() - 5.minutes)
        val newer = Clock.System.now()
        repo.write("k", newer)
        assertEquals(newer, repo.read("k"))
    }

    @Test
    fun differentKeys_isolated() = runTest {
        val repo = FakeFetchedAtRepository()
        val a = Clock.System.now()
        val b = a - 10.minutes
        repo.write("alpha", a)
        repo.write("beta", b)
        assertEquals(a, repo.read("alpha"))
        assertEquals(b, repo.read("beta"))
    }
}
