package org.mifos.mobilewallet.core.utils

import android.util.Log
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * Created by ishankhanna on 30/05/14.
 *
 * This is a helper class that will be used to convert List<Interger> Type Dates
 * from MifosX into Simple Strings or Date Formats</Interger>
 */
object DateHelper {
    val LOG_TAG = DateHelper::class.java.simpleName
    const val FORMAT_dd_MMMM_yyyy = "dd MMMM yyyy"
    const val DD_MMM_YYYY = "dd MMM yyyy"
    const val DD_MM_YYYY = "dd-MM-yyyy"

    /**
     * the result string uses the list given in a reverse order ([x, y, z] results in "z y x")
     *
     * @param integersOfDate [year-month-day] (ex [2016, 4, 14])
     * @return date in the format day month year (ex 14 Apr 2016)
     */

    @JvmStatic
    fun getDateAsString(integersOfDate: List<Int>): String {
        val stringBuilder = StringBuilder()
        stringBuilder.append(integersOfDate[2])
            .append(' ')
            .append(getMonthName(integersOfDate[1]))
            .append(' ')
            .append(integersOfDate[0])
        return stringBuilder.toString()
    }

    fun getDateAsString(integersOfDate: List<Int>, pattern: String?): String {
        return getFormatConverter(
            DD_MMM_YYYY,
            pattern, getDateAsString(integersOfDate)
        )
    }

    /**
     * This Method converting the dd-MM-yyyy format type date string into dd MMMM yyyy
     *
     * @param format     Final Format of date string
     * @param dateString date string
     * @return dd MMMM yyyy format date string.
     */
    fun getSpecificFormat(format: String?, dateString: String?): String {
        val pickerFormat = SimpleDateFormat(DD_MM_YYYY, Locale.ENGLISH)
        val finalFormat = SimpleDateFormat(format, Locale.ENGLISH)
        var date: Date? = null
        try {
            date = pickerFormat.parse(dateString)
        } catch (e: ParseException) {
            Log.d(LOG_TAG, e.localizedMessage)
        }
        return finalFormat.format(date)
    }

    fun getFormatConverter(
        currentFormat: String?, requiredFormat: String?,
        dateString: String?
    ): String {
        val pickerFormat = SimpleDateFormat(currentFormat, Locale.ENGLISH)
        val finalFormat = SimpleDateFormat(requiredFormat, Locale.ENGLISH)
        var date: Date? = null
        try {
            date = pickerFormat.parse(dateString)
        } catch (e: ParseException) {
            Log.d(LOG_TAG, e.localizedMessage)
        }
        return finalFormat.format(date)
    }

    /**
     * @param month an integer from 1 to 12
     * @return string representation of the month like Jan or Feb..etc
     */
    fun getMonthName(month: Int): String {
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

    fun getDateAsLongFromString(dateStr: String?, pattern: String?): Long {
        val sdf = SimpleDateFormat(pattern)
        var date: Date? = null
        try {
            date = sdf.parse(dateStr)
        } catch (e: ParseException) {
            Log.d("TAG", e.message!!)
        }
        return date!!.time
    }

    fun getDateAsLongFromList(integersOfDate: List<Int>): Long {
        val dateStr = getDateAsString(integersOfDate)
        return getDateAsLongFromString(dateStr, DD_MMM_YYYY)
    }

    fun subtractWeeks(number: Int): Long {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.WEEK_OF_YEAR, -number)
        return calendar.timeInMillis
    }

    fun subtractMonths(number: Int): Long {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.MONTH, -number)
        return calendar.timeInMillis
    }

    @JvmStatic
    fun getDateAsStringFromLong(timeInMillis: Long): String {
        val sdf = SimpleDateFormat(DD_MMM_YYYY)
        return sdf.format(Date(timeInMillis))
    }
}