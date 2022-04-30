package com.example.fitnessapptabbed.util

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
        private fun getCurrentDateTime(): Date = Calendar.getInstance().time

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
         */
        @JvmStatic
        fun getCurrentDateInString(format: String = defaultFormat, locale: Locale = Locale.getDefault()): String {
            val date = getCurrentDateTime()
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