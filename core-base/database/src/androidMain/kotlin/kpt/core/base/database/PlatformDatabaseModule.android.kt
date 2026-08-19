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
import org.koin.android.ext.koin.androidApplication
import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * Android Koin module that builds the fork's [RoomDatabase] singleton [T].
 *
 * INFRA (owner: template). Encapsulates the platform SQLite driver, query dispatcher
 * and destructive-migration fallback policy so a fork's `core/database` never re-authors
 * this boilerplate — it only binds its concrete database type ([T]) and passes the
 * generated [naming]. See `kpt.core.database.di.DatabaseModule`.
 */
inline fun <reified T : RoomDatabase> platformDatabaseModule(naming: DatabaseNaming): Module = module {
    single<T> {
        AppDatabaseFactory(androidApplication())
            .createDatabase<T>(databaseName = naming.fileName)
            .fallbackToDestructiveMigration(dropAllTables = true)
            .fallbackToDestructiveMigrationOnDowngrade(false)
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .build()
    }
}
