package com.example.fitnessapptabbed.util

import android.os.SystemClock
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * DateTime utilities class
 * @author Long
 * @version 1.0
 */
class DateTime {
    companion object {
        private const val defaultFormat = "dd.MM.yyyy"

        /**
         * Method returns [Date] with current time
         */
        @JvmStatic
        private fun getCurrentDateTime(offset: Long = 0): Date {
            return Date(System.currentTimeMillis() - offset)
        }

        /**
         * Method extension, formats [Date] to [format]
         */
        @JvmStatic
        private fun Date.toString(format: String, locale: Locale = Locale.getDefault()): String {
            val formatter = SimpleDateFormat(format, locale)
            return formatter.format(this)
        }

        /**
         * Method return current [Date] and time in [format] and [locale]
         * if [offset] is inputted then method returns (current time - offset)
         */
        @JvmStatic
        @JvmOverloads
        fun getCurrentDateInString(format: String = defaultFormat, offset: Long = 0, locale: Locale = Locale.getDefault()): String {
            val date = getCurrentDateTime(offset)
            return date.toString(format, locale)
        }

        /**
         * Method return a [String] representation of elapsed time from [millis]
         */
        @JvmStatic
        fun getTimeFromLong(millis: Long): String {
            return String.format("%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(millis),
                TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1),
                TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1)
            )
        }
    }
}