/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
plugins {
    alias(libs.plugins.kmp.library.convention)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            // The generic NotificationScheduler<T : NotificationRequest> contract lives in
            // core-base/platform — BillReminderSchedule implements NotificationRequest from
            // there, so consumers only see the typed domain payload.
            api(projects.coreBase.platform)

            implementation(libs.kotlinx.coroutines.core)
        }

        androidMain.dependencies {
            // Bill-reminder notification scheduling — WorkManager + NotificationCompat power
            // the Android actual of kpt.core.platform.notification.bill.BillReminderScheduler.
            implementation(libs.androidx.work.ktx)
            implementation(libs.androidx.core.ktx)

            // koin-android exposes androidContext() for Koin scope wiring in
            // notification/bill/di/NotificationModule.android.kt.
            implementation(libs.koin.android)
        }
    }
}
