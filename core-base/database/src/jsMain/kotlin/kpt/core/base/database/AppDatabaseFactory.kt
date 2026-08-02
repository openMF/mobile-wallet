/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package kpt.core.base.database

import androidx.room3.Room
import androidx.room3.RoomDatabase
import androidx.sqlite.driver.web.WebWorkerSQLiteDriver
import org.w3c.dom.Worker

/**
 * JS (Kotlin/JS) factory for creating Room 3 database instances.
 *
 * Workers are bundled locally via webpack (no CDN dependency) using the
 * `sqlite-wasm-worker` npm package declared in core-base/database/build.gradle.kts.
 * webpack resolves `new URL("sqlite-wasm-worker/worker.js", import.meta.url)` at
 * build time and emits the worker + its WASM dependency as a separate bundle.
 *
 * [createDatabase] auto-detects the runtime environment:
 *  - crossOriginIsolated = true  (COOP/COEP headers — localhost dev) →
 *    WebWorkerSQLiteDriver + OPFS-backed persistent storage.
 *  - crossOriginIsolated = false (GitHub Pages / no headers) →
 *    Room.inMemoryDatabaseBuilder — data lives only for the current page session.
 */
@PublishedApi
internal fun isCrossOriginIsolated(): Boolean =
    js("self.crossOriginIsolated === true").unsafeCast<Boolean>()

// @PublishedApi internal — referenced by the public `inline fun createDatabase`
// below. Kotlin's JS compiler rejects public inline functions that touch
// `private` symbols (`Public-API inline function cannot access non-public-API
// function`), so the helper has to be at least internal + @PublishedApi. Same
// pattern as `isCrossOriginIsolated` above and as the wasmJs sibling factory.
@PublishedApi
internal fun createSQLiteWasmWorker(): Worker =
    Worker(js("""new URL("sqlite-wasm-worker/worker.js", import.meta.url)"""))

/**
 * Alternative driver backed by sql.js (in-memory only, broader browser compatibility).
 * Switch by calling `createSqlJsWorker()` in `createDatabase` / `createInMemoryDatabase`.
 */
@PublishedApi
@Suppress("unused")
internal fun createSqlJsWorker(): Worker =
    Worker(js("""new URL("sql-js-worker/worker.js", import.meta.url)"""))

class AppDatabaseFactory {

    inline fun <reified T : RoomDatabase> createDatabase(
        databaseName: String,
    ): RoomDatabase.Builder<T> {
        return if (isCrossOriginIsolated()) {
            Room.databaseBuilder<T>(name = databaseName)
                .setDriver(WebWorkerSQLiteDriver(createSQLiteWasmWorker()))
        } else {
            Room.inMemoryDatabaseBuilder<T>()
        }
    }

    inline fun <reified T : RoomDatabase> createInMemoryDatabase(): RoomDatabase.Builder<T> =
        Room.inMemoryDatabaseBuilder<T>()
}
