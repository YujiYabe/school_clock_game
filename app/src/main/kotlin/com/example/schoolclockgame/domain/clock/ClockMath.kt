package com.example.schoolclockgame.domain.clock

import androidx.compose.ui.geometry.Offset
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

private const val MinutesPerDay = 24 * 60
private const val DegreesPerMinute = 6f
private const val DegreesPerHour = 30f

object ClockMath {
    fun normalizeHour(hour: Int): Int = ((hour % 24) + 24) % 24

    fun normalizeMinute(minute: Int): Int = ((minute % 60) + 60) % 60

    fun toTotalMinutes(hours: Int, minutes: Int): Int {
        return normalizeHour(hours) * 60 + normalizeMinute(minutes)
    }

    fun fromTotalMinutes(totalMinutes: Int): ClockTime {
        val normalized = ((totalMinutes % MinutesPerDay) + MinutesPerDay) % MinutesPerDay
        return ClockTime(
            hours = normalized / 60,
            minutes = normalized % 60,
        )
    }

    fun minuteHandAngleDegrees(minutes: Int): Float {
        return normalizeMinute(minutes) * DegreesPerMinute
    }

    fun hourHandAngleDegrees(hours: Int, minutes: Int): Float {
        val hour12 = normalizeHour(hours) % 12
        return hour12 * DegreesPerHour + normalizeMinute(minutes) * 0.5f
    }

    fun pointToClockAngleDegrees(point: Offset, center: Offset): Float {
        val radians = atan2(point.y - center.y, point.x - center.x)
        val degreesFromThreeOClock = radians * 180f / PI.toFloat()
        return (degreesFromThreeOClock + 90f).floorMod(360f)
    }

    fun pointOnCircle(center: Offset, radius: Float, clockAngleDegrees: Float): Offset {
        val radians = (clockAngleDegrees - 90f) * PI.toFloat() / 180f
        return Offset(
            x = center.x + cos(radians) * radius,
            y = center.y + sin(radians) * radius,
        )
    }

    fun minutesFromAngle(angleDegrees: Float): Int {
        return (angleDegrees / DegreesPerMinute).roundToInt().floorMod(60)
    }

    fun totalMinutesFromHourHandAngle(angleDegrees: Float, keepPm: Boolean): Int {
        val minutesInHalfDay = (angleDegrees.floorMod(360f) / 360f * 12f * 60f).roundToInt()
        val halfDayOffset = if (keepPm) 12 * 60 else 0
        return halfDayOffset + minutesInHalfDay
    }

    fun clockwiseDeltaDegrees(previousAngle: Float, nextAngle: Float): Float {
        var delta = nextAngle - previousAngle
        if (delta > 180f) delta -= 360f
        if (delta < -180f) delta += 360f
        return delta
    }

    fun minutesDeltaFromAngleDelta(deltaDegrees: Float): Int {
        return (deltaDegrees / DegreesPerMinute).roundToInt()
    }

    fun snapTotalMinutes(totalMinutes: Int, snapInterval: Int): Int {
        val interval = snapInterval.coerceIn(1, 60)
        return (totalMinutes.toFloat() / interval).roundToInt() * interval
    }

    fun minuteSweepDegrees(minutes: Int): Float = normalizeMinute(minutes) * DegreesPerMinute

    private fun Float.floorMod(mod: Float): Float {
        val result = this % mod
        return if (result < 0f) result + mod else result
    }

    private fun Int.floorMod(mod: Int): Int {
        val result = this % mod
        return if (result < 0) result + mod else result
    }
}

data class ClockTime(
    val hours: Int,
    val minutes: Int,
)
