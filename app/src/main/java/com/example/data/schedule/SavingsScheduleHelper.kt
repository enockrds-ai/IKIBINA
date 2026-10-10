package com.example.data.schedule

import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

object SavingsScheduleHelper {

    val RWANDA_TIMEZONE: TimeZone = TimeZone.getTimeZone("Africa/Kigali")

    /**
     * Checks if given timestamp (or current time) is within the weekly savings window:
     * Tuesday 17:00 to Wednesday 15:00 Rwanda local time.
     */
    fun isWithinSavingsPeriod(timestampMs: Long = System.currentTimeMillis()): Boolean {
        val cal = Calendar.getInstance(RWANDA_TIMEZONE, Locale.US).apply {
            timeInMillis = timestampMs
        }
        val dayOfWeek = cal.get(Calendar.DAY_OF_WEEK)
        val hour = cal.get(Calendar.HOUR_OF_DAY)
        val minute = cal.get(Calendar.MINUTE)

        return when (dayOfWeek) {
            Calendar.TUESDAY -> {
                // Starts Tuesday at 17:00
                hour > 17 || (hour == 17 && minute >= 0)
            }
            Calendar.WEDNESDAY -> {
                // Ends Wednesday at 15:00
                hour < 15 || (hour == 15 && minute == 0)
            }
            else -> false
        }
    }

    /**
     * Produces a standardized week identifier, e.g. "2026-W41".
     */
    fun getWeekCode(timestampMs: Long = System.currentTimeMillis()): String {
        val cal = Calendar.getInstance(RWANDA_TIMEZONE, Locale.US).apply {
            timeInMillis = timestampMs
        }
        val year = cal.get(Calendar.YEAR)
        val week = cal.get(Calendar.WEEK_OF_YEAR)
        return "%04d-W%02d".format(year, week)
    }

    /**
     * Human-readable schedule banner in Kinyarwanda.
     */
    fun getScheduleStatusText(timestampMs: Long = System.currentTimeMillis()): Pair<Boolean, String> {
        val isOpen = isWithinSavingsPeriod(timestampMs)
        return if (isOpen) {
            Pair(true, "Igihe cyo kuzigama KIRAFUNGUYE: Ku wa kabiri 17:00 – Ku wa gatatu 15:00 (Nta gihano)")
        } else {
            Pair(false, "Igihe cyo kuzigama CYARAFUNZWE. Kuzigama ubu bizakurura igihano cya RWF 100 cy'icyumweru.")
        }
    }
}
