package com.example.schoolclockgame.presentation.clock

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.schoolclockgame.domain.calendar.DateMath
import com.example.schoolclockgame.domain.clock.ClockMath
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class ClockViewModel : ViewModel() {
    private val _state = MutableStateFlow(ClockState.now())
    val state: StateFlow<ClockState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            while (true) {
                delay(1_000)
                if (_state.value.isLiveClockRunning) {
                    setCurrentDateTime(isRunning = true)
                }
            }
        }
    }

    fun resumeCurrentTime() {
        setCurrentDateTime(isRunning = true)
    }

    fun pauseClock() {
        _state.update { it.copy(isLiveClockRunning = false) }
    }

    fun setLearningArea(learningArea: LearningArea) {
        _state.update { it.copy(learningArea = learningArea) }
    }

    fun setDisplayMode(displayMode: ClockDisplayMode) {
        _state.update { current ->
            when (displayMode) {
                ClockDisplayMode.HOURS_ONLY -> current.copy(
                    displayMode = displayMode,
                    isHelperTextVisible = false,
                    isAreaHighlightVisible = true,
                    snapInterval = 15,
                )

                ClockDisplayMode.MINUTES_ONLY -> current.copy(
                    displayMode = displayMode,
                    isHelperTextVisible = true,
                    isAreaHighlightVisible = true,
                    snapInterval = 5,
                )

                ClockDisplayMode.FULL -> current.copy(
                    displayMode = displayMode,
                    isHelperTextVisible = true,
                    isAreaHighlightVisible = false,
                    snapInterval = 1,
                )
            }
        }
    }

    fun setHelperTextVisible(visible: Boolean) {
        _state.update { it.copy(isHelperTextVisible = visible) }
    }

    fun setAreaHighlightVisible(visible: Boolean) {
        _state.update { it.copy(isAreaHighlightVisible = visible) }
    }

    fun setSnapInterval(minutes: Int) {
        val allowed = when (minutes) {
            1, 5, 15 -> minutes
            else -> 1
        }
        _state.update { it.copy(snapInterval = allowed) }
    }

    fun setTime(hours: Int, minutes: Int, seconds: Int = _state.value.seconds) {
        val time = ClockMath.fromTotalMinutes(ClockMath.toTotalMinutes(hours, minutes))
        _state.update {
            it.copy(
                hours = time.hours,
                minutes = time.minutes,
                seconds = seconds.coerceIn(0, 59),
                isLiveClockRunning = false,
            )
        }
    }

    fun setTotalMinutes(totalMinutes: Int, snap: Boolean = false) {
        val current = _state.value
        val adjusted = if (snap) {
            ClockMath.snapTotalMinutes(totalMinutes, current.snapInterval)
        } else {
            totalMinutes
        }
        val time = ClockMath.fromTotalMinutes(adjusted)
        _state.update {
            it.copy(
                hours = time.hours,
                minutes = time.minutes,
                isLiveClockRunning = false,
            )
        }
    }

    fun snapCurrentTime() {
        val current = _state.value
        setTotalMinutes(
            totalMinutes = ClockMath.toTotalMinutes(current.hours, current.minutes),
            snap = true,
        )
    }

    fun setDate(year: Int, month: Int, day: Int) {
        val date = DateMath.normalizeDate(year, month, day)
        _state.update {
            it.copy(
                year = date.year,
                month = date.month,
                day = date.day,
                isLiveClockRunning = false,
            )
        }
    }

    fun moveDay(days: Int) {
        val current = _state.value
        val date = DateMath.plusDays(current.year, current.month, current.day, days.toLong())
        setDate(date.year, date.month, date.day)
    }

    fun moveMonth(months: Int) {
        val current = _state.value
        val date = DateMath.plusMonths(current.year, current.month, current.day, months.toLong())
        setDate(date.year, date.month, date.day)
    }

    fun moveYear(years: Int) {
        val current = _state.value
        val date = DateMath.plusYears(current.year, current.month, current.day, years.toLong())
        setDate(date.year, date.month, date.day)
    }

    fun moveHour(hours: Int) {
        moveDateTime { it.plusHours(hours.toLong()) }
    }

    fun moveMinute(minutes: Int) {
        moveDateTime { it.plusMinutes(minutes.toLong()) }
    }

    fun moveSecond(seconds: Int) {
        moveDateTime { it.plusSeconds(seconds.toLong()) }
    }

    fun moveAmount(unit: DateTimeAmountUnit, amount: Int) {
        when (unit) {
            DateTimeAmountUnit.YEAR -> moveYear(amount)
            DateTimeAmountUnit.MONTH -> moveMonth(amount)
            DateTimeAmountUnit.DAY -> moveDay(amount)
            DateTimeAmountUnit.HOUR -> moveHour(amount)
            DateTimeAmountUnit.MINUTE -> moveMinute(amount)
            DateTimeAmountUnit.SECOND -> moveSecond(amount)
        }
    }

    fun setYear(year: Int) {
        val current = _state.value
        setDate(year, current.month, current.day)
    }

    fun setMonth(month: Int) {
        val current = _state.value
        setDate(current.year, month, current.day)
    }

    fun setDay(day: Int) {
        val current = _state.value
        setDate(current.year, current.month, day)
    }

    fun setHourValue(hour: Int) {
        _state.update { it.copy(hours = hour.coerceIn(0, 23), isLiveClockRunning = false) }
    }

    fun setMinuteValue(minute: Int) {
        _state.update { it.copy(minutes = minute.coerceIn(0, 59), isLiveClockRunning = false) }
    }

    fun setSecondValue(second: Int) {
        _state.update { it.copy(seconds = second.coerceIn(0, 59), isLiveClockRunning = false) }
    }

    fun setHourFromDial(value: Int) {
        _state.update { it.copy(hours = value.coerceIn(0, 23), isLiveClockRunning = false) }
    }

    fun setMinuteFromDial(value: Int) {
        _state.update {
            it.copy(
                minutes = value.coerceIn(0, 59),
                isLiveClockRunning = false,
            )
        }
    }

    fun setSecondFromDial(value: Int) {
        _state.update {
            it.copy(
                seconds = value.coerceIn(0, 59),
                isLiveClockRunning = false,
            )
        }
    }

    private fun moveDateTime(transform: (LocalDateTime) -> LocalDateTime) {
        val current = _state.value
        val next = transform(
            LocalDateTime.of(
                current.year,
                current.month,
                current.day,
                current.hours,
                current.minutes,
                current.seconds,
            ),
        )
        _state.update {
            it.copy(
                year = next.year,
                month = next.monthValue,
                day = next.dayOfMonth,
                hours = next.hour,
                minutes = next.minute,
                seconds = next.second,
                isLiveClockRunning = false,
            )
        }
    }

    private fun setCurrentDateTime(isRunning: Boolean) {
        val now = LocalDateTime.now()
        _state.update {
            it.copy(
                year = now.year,
                month = now.monthValue,
                day = now.dayOfMonth,
                hours = now.hour,
                minutes = now.minute,
                seconds = now.second,
                isLiveClockRunning = isRunning,
            )
        }
    }
}
