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
import kotlinx.coroutines.Dispatchers
import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * JS (Kotlin/JS) Koin module that builds the fork's [RoomDatabase] singleton [T].
 *
 * INFRA (owner: template). The driver is chosen by [AppDatabaseFactory] itself
 * (WebWorker + OPFS when cross-origin-isolated, in-memory otherwise), so only the query
 * dispatcher is applied here. Keeps `core/database` free of platform builder code.
 * See `kpt.core.database.di.DatabaseModule`.
 */
inline fun <reified T : RoomDatabase> platformDatabaseModule(naming: DatabaseNaming): Module = module {
    single<T> {
        AppDatabaseFactory()
            .createDatabase<T>(databaseName = naming.fileName)
            .setQueryCoroutineContext(Dispatchers.Default)
            .build()
    }
}
