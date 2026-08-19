/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package kpt.core.platform.notification.bill

import kotlinx.cinterop.ExperimentalForeignApi
import kpt.core.base.platform.notification.NotificationScheduler
import platform.Foundation.NSDate
import platform.Foundation.dateWithTimeIntervalSinceNow
import platform.Foundation.timeIntervalSince1970
import platform.UserNotifications.UNAuthorizationOptionAlert
import platform.UserNotifications.UNAuthorizationOptionBadge
import platform.UserNotifications.UNAuthorizationOptionSound
import platform.UserNotifications.UNCalendarNotificationTrigger
import platform.UserNotifications.UNMutableNotificationContent
import platform.UserNotifications.UNNotificationRequest
import platform.UserNotifications.UNUserNotificationCenter
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * iOS implementation backed by `UNUserNotificationCenter`.
 *
 * **Scheduling**: builds an [UNCalendarNotificationTrigger] for the exact `triggerAtMs` instant —
 * iOS delivers the notification at that wall-clock moment regardless of app lifecycle, provided
 * the user has granted notification authorization.
 *
 * **Permission**: the first call to [schedule] proactively requests
 * `[alert, badge, sound]` authorization. If the user denies, [UNUserNotificationCenter.addNotificationRequest]
 * silently no-ops on subsequent calls — the bill stays persisted and the in-app fallback
 * fires on next app open.
 *
 * **Idempotency**: requests are tagged with `billId` as the identifier; iOS replaces a
 * pending request with the same identifier rather than queueing two — re-scheduling after
 * an edit is safe.
 *
 * **Past instants**: matched against `NSDate()` here so we don't fire a stale notification
 * on cold-start. Callers compute the next occurrence.
 *
 * **Background limits**: iOS imposes an upper bound (~64) on pending local notifications
 * per app. Heavy users (many bills × many months ahead) should re-evaluate on app launch
 * rather than schedule a year of occurrences eagerly.
 */
@OptIn(ExperimentalForeignApi::class)
actual class BillReminderScheduler : NotificationScheduler<BillReminderSchedule> {

    private val center: UNUserNotificationCenter = UNUserNotificationCenter.currentNotificationCenter()

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    actual override suspend fun schedule(bill: BillReminderSchedule) {
        ensureAuthorization()
        val triggerSeconds = bill.triggerAtMs / 1000.0
        val nowSeconds = NSDate().timeIntervalSince1970
        val delaySeconds = triggerSeconds - nowSeconds
        if (delaySeconds <= 0.0) return
        // K/N maps NSDate's static factory `+dateWithTimeIntervalSinceNow:` to the
        // top-level companion factory `dateWithTimeIntervalSinceNow` in the iOS Foundation
        // bindings (imported above). Constructing by offsetting from "now" dodges the
        // static-import friction with the SinceReferenceDate / Since1970 overloads.
        val triggerDate = NSDate.dateWithTimeIntervalSinceNow(delaySeconds)

        val content = UNMutableNotificationContent().apply {
            setTitle(bill.title)
            setBody(bill.body)
        }
        // UNCalendarNotificationTrigger fires when wall-clock matches the supplied date
        // components. We construct from NSDateComponents that include year..second so the
        // trigger is anchored to one specific instant (non-repeating).
        val components = platform.Foundation.NSCalendar.currentCalendar.components(
            unitFlags = (
                NSCalendarYear or
                    NSCalendarMonth or
                    NSCalendarDay or
                    NSCalendarHour or
                    NSCalendarMinute or
                    NSCalendarSecond
                ),
            fromDate = triggerDate,
        )
        val trigger = UNCalendarNotificationTrigger.triggerWithDateMatchingComponents(
            dateComponents = components,
            repeats = false,
        )
        val request = UNNotificationRequest.requestWithIdentifier(
            identifier = identifier(bill.billId),
            content = content,
            trigger = trigger,
        )
        suspendCoroutine<Unit> { cont ->
            center.addNotificationRequest(request) { _ -> cont.resume(Unit) }
        }
    }

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    actual override suspend fun cancel(billId: String) {
        center.removePendingNotificationRequestsWithIdentifiers(listOf(identifier(billId)))
    }

    actual override suspend fun cancelAll() {
        center.removeAllPendingNotificationRequests()
    }

    /** Request user authorization once per app lifecycle; failures silently no-op subsequent schedules. */
    private suspend fun ensureAuthorization() {
        suspendCoroutine<Unit> { cont ->
            center.requestAuthorizationWithOptions(
                options = UNAuthorizationOptionAlert or UNAuthorizationOptionBadge or UNAuthorizationOptionSound,
                completionHandler = { _, _ -> cont.resume(Unit) },
            )
        }
    }

    private fun identifier(billId: String): String = "bill_reminder_$billId"

    // Constant names mirror the Apple Foundation `NSCalendarUnit` enum (PascalCase) so the
    // call sites read 1:1 with Apple's documentation rather than translated SCREAMING_SNAKE_CASE.
    @Suppress("ktlint:standard:property-naming")
    private companion object {
        // NSCalendar unit flag constants are typed as ULong in K/N — collapse the typed
        // constants here so the public schedule() body stays readable.
        const val NSCalendarYear: ULong = 4uL
        const val NSCalendarMonth: ULong = 8uL
        const val NSCalendarDay: ULong = 16uL
        const val NSCalendarHour: ULong = 32uL
        const val NSCalendarMinute: ULong = 64uL
        const val NSCalendarSecond: ULong = 128uL
    }
}
