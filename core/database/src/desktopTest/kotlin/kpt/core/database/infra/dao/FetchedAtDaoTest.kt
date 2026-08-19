/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package kpt.core.database.infra.dao

import androidx.room3.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import kpt.core.base.database.infra.dao.FetchedAtDao
import kpt.core.base.database.infra.entity.FetchedAtEntity
import kpt.core.database.AppDatabase
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

/**
 * Verifies the framework-owned `framework_fetched_at` table backing
 * [kpt.core.base.store.FetchedAtRepository] via [FetchedAtDao].
 *
 * Locks the contract for warm-reopen of paginated and single-key streams: write
 * a timestamp on every successful network fetch, read it back on subsequent
 * stream construction so the staleness banner shows real "Updated 5m ago".
 */
class FetchedAtDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var dao: FetchedAtDao

    @BeforeTest
    fun setup() {
        database = Room.inMemoryDatabaseBuilder<AppDatabase>()
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .build()
        dao = database.fetchedAtDao
    }

    @AfterTest
    fun teardown() {
        database.close()
    }

    @Test
    fun readUnknownKeyReturnsNull() = runTest {
        assertNull(dao.read("never-written"))
    }

    @Test
    fun upsertThenReadReturnsTimestamp() = runTest {
        val now = 1_762_000_000_000L
        dao.upsert(FetchedAtEntity(storeKey = "crypto:coinMarkets", lastFetchedMillis = now))
        assertEquals(now, dao.read("crypto:coinMarkets"))
    }

    @Test
    fun upsertOverwritesPreviousValue() = runTest {
        dao.upsert(FetchedAtEntity(storeKey = "k", lastFetchedMillis = 100L))
        dao.upsert(FetchedAtEntity(storeKey = "k", lastFetchedMillis = 200L))
        assertEquals(200L, dao.read("k"))
    }

    @Test
    fun differentKeysIsolated() = runTest {
        dao.upsert(FetchedAtEntity(storeKey = "alpha", lastFetchedMillis = 111L))
        dao.upsert(FetchedAtEntity(storeKey = "beta", lastFetchedMillis = 222L))
        assertEquals(111L, dao.read("alpha"))
        assertEquals(222L, dao.read("beta"))
    }
}
