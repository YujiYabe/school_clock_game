package com.example.schoolclockgame.presentation.clock

import java.time.LocalDateTime

enum class ClockDisplayMode {
    HOURS_ONLY,
    MINUTES_ONLY,
    FULL,
}

enum class LearningArea {
    CLOCK,
    CALENDAR,
}

enum class DateTimeAmountUnit {
    YEAR,
    MONTH,
    DAY,
    HOUR,
    MINUTE,
    SECOND,
}

data class ClockState(
    val hours: Int = 7,
    val minutes: Int = 0,
    val seconds: Int = 0,
    val displayMode: ClockDisplayMode = ClockDisplayMode.HOURS_ONLY,
    val isHelperTextVisible: Boolean = false,
    val isAreaHighlightVisible: Boolean = true,
    val snapInterval: Int = 5,
    val learningArea: LearningArea = LearningArea.CLOCK,
    val year: Int = 2026,
    val month: Int = 4,
    val day: Int = 1,
    val isLiveClockRunning: Boolean = true,
) {
    val digitalText: String
        get() = "%02d:%02d:%02d".format(hours, minutes, seconds)

    val dateText: String
        get() = "%04d/%02d/%02d".format(year, month, day)

    val dateTimeText: String
        get() = "$dateText $digitalText"

    companion object {
        fun now(): ClockState {
            val now = LocalDateTime.now()
            return ClockState(
                year = now.year,
                month = now.monthValue,
                day = now.dayOfMonth,
                hours = now.hour,
                minutes = now.minute,
                seconds = now.second,
                isLiveClockRunning = true,
            )
        }
    }
}
