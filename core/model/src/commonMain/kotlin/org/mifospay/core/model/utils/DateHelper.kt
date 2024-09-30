/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.core.model.utils

import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.format
import kotlinx.datetime.format.FormatStringsInDatetimeFormats
import kotlinx.datetime.format.byUnicodePattern
import kotlinx.datetime.minus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Duration.Companion.days

@OptIn(FormatStringsInDatetimeFormats::class)
object DateHelper {
    private const val LOG_TAG = "DateHelper"
    private const val FULL_MONTH = "dd MMM yyyy"
    private const val SHORT_MONTH = "dd-MM-yyyy"

    private val fullMonthFormat = LocalDateTime.Format {
        byUnicodePattern(FULL_MONTH)
    }

    private val shortMonthFormat = LocalDateTime.Format {
        byUnicodePattern(SHORT_MONTH)
    }

    /**
     * the result string uses the list given in a reverse order ([x, y, z] results in "z y x")
     *
     * @param integersOfDate [year-month-day] (ex [2016, 4, 14])
     * @return date in the format day month year (ex 14 Apr 2016)
     */
    fun getDateAsString(integersOfDate: List<Int>): String {
        val stringBuilder = StringBuilder()
        stringBuilder.append(integersOfDate[2])
            .append(' ')
            .append(getMonthName(integersOfDate[1]))
            .append(' ')
            .append(integersOfDate[0])
        return stringBuilder.toString()
    }

    fun getDateAsString(integersOfDate: List<Int>, pattern: String): String {
        return getFormatConverter(FULL_MONTH, pattern, getDateAsString(integersOfDate))
    }

    /**
     * This Method converting the dd-MM-yyyy format type date string into dd MMMM yyyy
     *
     * @param format     Final Format of date string
     * @param dateString date string
     * @return dd MMMM yyyy format date string.
     */
    fun getSpecificFormat(format: String, dateString: String): String {
        val pickerFormat = shortMonthFormat
        val finalFormat = LocalDateTime.Format { byUnicodePattern(format) }

        return finalFormat.format(pickerFormat.parse(dateString))
    }

    fun getFormatConverter(
        currentFormat: String,
        requiredFormat: String,
        dateString: String,
    ): String {
        val pickerFormat = LocalDateTime.Format { byUnicodePattern(currentFormat) }
        val finalFormat = LocalDateTime.Format { byUnicodePattern(requiredFormat) }

        return pickerFormat.parse(dateString).format(finalFormat)
    }

    /**
     * @param month an integer from 1 to 12
     * @return string representation of the month like Jan or Feb..etc
     */
    private fun getMonthName(month: Int): String {
        var monthName = ""
        when (month) {
            1 -> monthName = "Jan"
            2 -> monthName = "Feb"
            3 -> monthName = "Mar"
            4 -> monthName = "Apr"
            5 -> monthName = "May"
            6 -> monthName = "Jun"
            7 -> monthName = "Jul"
            8 -> monthName = "Aug"
            9 -> monthName = "Sep"
            10 -> monthName = "Oct"
            11 -> monthName = "Nov"
            12 -> monthName = "Dec"
        }
        return monthName
    }

    private fun getDateAsLongFromString(dateStr: String, pattern: String): Long {
        return try {
            // Create a DateTimeFormatter with the given pattern
            val formatter = LocalDateTime.Format { byUnicodePattern(pattern) }

            // Parse the string to a LocalDateTime
            val localDateTime = LocalDateTime.parse(dateStr, formatter)

            // Convert LocalDateTime to Instant (assuming the date is in the system's time zone)
            val instant = localDateTime.toInstant(TimeZone.currentSystemDefault())

            // Convert Instant to milliseconds since epoch
            instant.toEpochMilliseconds()
        } catch (e: Exception) {
            0L
        }
    }

    fun getDateAsLongFromList(integersOfDate: List<Int>): Long {
        val dateStr = getDateAsString(integersOfDate)
        return getDateAsLongFromString(dateStr, FULL_MONTH)
    }

    fun subtractWeeks(number: Int): Long {
        val now = Clock.System.now()
        val subtracted = now.minus(number.times(7).days)
        return subtracted.toEpochMilliseconds()
    }

    fun subtractMonths(number: Int): Long {
        val now = Clock.System.now()
        val currentDate = now.toLocalDateTime(TimeZone.currentSystemDefault()).date
        val subtractedDate = currentDate.minus(number, DateTimeUnit.MONTH)
        return subtractedDate.atStartOfDayIn(TimeZone.currentSystemDefault()).toEpochMilliseconds()
    }

    fun getDateAsStringFromLong(timeInMillis: Long): String {
        return fullMonthFormat.parse(timeInMillis.toString()).toString()
    }

    val currentDate = Clock.System.now().toEpochMilliseconds().toString()
    val formattedDate = fullMonthFormat.parse(currentDate).toString()
}
