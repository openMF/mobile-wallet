/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package kpt.core.base.database

import androidx.room3.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers
import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * Desktop (JVM) Koin module that builds the fork's [RoomDatabase] singleton [T].
 *
 * INFRA (owner: template). The OS-appropriate data directory is derived from the
 * generated [naming].desktopDirName (per-fork, collision-free); driver, dispatcher and
 * destructive-migration fallback are template-owned here so `core/database` stays free
 * of platform builder code. See `kpt.core.database.di.DatabaseModule`.
 */
inline fun <reified T : RoomDatabase> platformDatabaseModule(naming: DatabaseNaming): Module = module {
    single<T> {
        AppDatabaseFactory(databaseDirName = naming.desktopDirName)
            .createDatabase<T>(databaseName = naming.fileName)
            .fallbackToDestructiveMigration(dropAllTables = true)
            .fallbackToDestructiveMigrationOnDowngrade(false)
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .build()
    }
}
