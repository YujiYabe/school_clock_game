package com.example.schoolclockgame.domain.calendar

import java.time.DateTimeException
import java.time.LocalDate

object DateMath {
    fun normalizeDate(year: Int, month: Int, day: Int): CalendarDate {
        val safeYear = year.coerceIn(1900, 2100)
        val safeMonth = month.coerceIn(1, 12)
        val safeDay = day.coerceIn(1, daysInMonth(safeYear, safeMonth))
        return CalendarDate(safeYear, safeMonth, safeDay)
    }

    fun plusDays(year: Int, month: Int, day: Int, days: Long): CalendarDate {
        return toLocalDate(year, month, day).plusDays(days).toCalendarDate()
    }

    fun plusMonths(year: Int, month: Int, day: Int, months: Long): CalendarDate {
        return toLocalDate(year, month, day).plusMonths(months).toCalendarDate()
    }

    fun plusYears(year: Int, month: Int, day: Int, years: Long): CalendarDate {
        return toLocalDate(year, month, day).plusYears(years).toCalendarDate()
    }

    fun daysInMonth(year: Int, month: Int): Int {
        return LocalDate.of(year.coerceIn(1900, 2100), month.coerceIn(1, 12), 1).lengthOfMonth()
    }

    fun isLeapYear(year: Int): Boolean {
        return LocalDate.of(year.coerceIn(1900, 2100), 1, 1).isLeapYear
    }

    fun dayOfYear(year: Int, month: Int, day: Int): Int {
        return toLocalDate(year, month, day).dayOfYear
    }

    fun daysInYear(year: Int): Int = if (isLeapYear(year)) 366 else 365

    fun monthProgress(year: Int, month: Int, day: Int): Float {
        return day.coerceIn(1, daysInMonth(year, month)).toFloat() / daysInMonth(year, month)
    }

    fun yearProgress(year: Int, month: Int, day: Int): Float {
        return dayOfYear(year, month, day).toFloat() / daysInYear(year)
    }

    private fun toLocalDate(year: Int, month: Int, day: Int): LocalDate {
        val normalized = normalizeDate(year, month, day)
        return try {
            LocalDate.of(normalized.year, normalized.month, normalized.day)
        } catch (_: DateTimeException) {
            LocalDate.of(normalized.year, normalized.month, 1)
        }
    }

    private fun LocalDate.toCalendarDate(): CalendarDate {
        return CalendarDate(year, monthValue, dayOfMonth)
    }
}

data class CalendarDate(
    val year: Int,
    val month: Int,
    val day: Int,
)
