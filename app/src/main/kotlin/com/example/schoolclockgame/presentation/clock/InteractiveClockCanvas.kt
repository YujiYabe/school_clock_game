package com.example.schoolclockgame.presentation.clock

import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntSize
import com.example.schoolclockgame.domain.clock.ClockMath
import kotlin.math.abs
import kotlin.math.min
import kotlin.math.roundToInt

private enum class ClockHand {
    Hour,
    Minute,
}

@Composable
fun InteractiveClockCanvas(
    state: ClockState,
    onTimeChange: (totalMinutes: Int, snap: Boolean) -> Unit,
    onDragFinished: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var activeHand by remember { mutableStateOf<ClockHand?>(null) }
    var previousAngle by remember { mutableFloatStateOf(0f) }
    var draggingTotalMinutes by remember { mutableFloatStateOf(0f) }

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .pointerInput(state.displayMode, state.hours, state.minutes) {
                detectDragGestures(
                    onDragStart = { offset ->
                        val center = centerOf(size)
                        val angle = ClockMath.pointToClockAngleDegrees(offset, center)
                        activeHand = pickHand(state, angle, offset, center, size)
                        previousAngle = angle
                        draggingTotalMinutes = ClockMath
                            .toTotalMinutes(state.hours, state.minutes)
                            .toFloat()
                    },
                    onDragEnd = {
                        onDragFinished()
                        activeHand = null
                    },
                    onDragCancel = {
                        onDragFinished()
                        activeHand = null
                    },
                    onDrag = { change, _ ->
                        val hand = activeHand ?: return@detectDragGestures
                        val center = centerOf(size)
                        val angle = ClockMath.pointToClockAngleDegrees(change.position, center)
                        when (hand) {
                            ClockHand.Minute -> {
                                val deltaDegrees = ClockMath.clockwiseDeltaDegrees(previousAngle, angle)
                                draggingTotalMinutes += deltaDegrees / 6f
                                onTimeChange(draggingTotalMinutes.roundToInt(), false)
                            }

                            ClockHand.Hour -> {
                                onTimeChange(
                                    ClockMath.totalMinutesFromHourHandAngle(
                                        angleDegrees = angle,
                                        keepPm = state.hours >= 12,
                                    ),
                                    false,
                                )
                            }
                        }
                        previousAngle = angle
                        change.consume()
                    },
                )
            },
    ) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val radius = min(size.width, size.height) * 0.46f
        val hourAngle = ClockMath.hourHandAngleDegrees(state.hours, state.minutes)
        val minuteAngle = ClockMath.minuteHandAngleDegrees(state.minutes)
        val showHourHand = state.displayMode != ClockDisplayMode.MINUTES_ONLY
        val showMinuteHand = state.displayMode != ClockDisplayMode.HOURS_ONLY

        drawCircle(Color(0xFFFFFBF2), radius, center)
        drawCircle(Color(0xFF2F3645), radius, center, style = Stroke(width = radius * 0.035f))

        if (state.displayMode == ClockDisplayMode.HOURS_ONLY && state.isAreaHighlightVisible) {
            val startHour = state.hours % 12
            drawArc(
                color = Color(0xFFFFB020).copy(alpha = 0.24f),
                startAngle = startHour * 30f - 90f,
                sweepAngle = 30f,
                useCenter = true,
                topLeft = Offset(center.x - radius, center.y - radius),
                size = Size(radius * 2f, radius * 2f),
            )
        }

        if (state.displayMode != ClockDisplayMode.HOURS_ONLY && state.isAreaHighlightVisible) {
            drawArc(
                color = Color(0xFF246BFD).copy(alpha = 0.18f),
                startAngle = -90f,
                sweepAngle = ClockMath.minuteSweepDegrees(state.minutes),
                useCenter = true,
                topLeft = Offset(center.x - radius, center.y - radius),
                size = Size(radius * 2f, radius * 2f),
            )
        }

        repeat(60) { index ->
            val isHourTick = index % 5 == 0
            val tickStart = if (isHourTick) radius * 0.86f else radius * 0.91f
            val tickEnd = radius * 0.97f
            val angle = index * 6f
            drawLine(
                color = if (isHourTick) Color(0xFF1F2430) else Color(0xFF9EA7B8),
                start = ClockMath.pointOnCircle(center, tickStart, angle),
                end = ClockMath.pointOnCircle(center, tickEnd, angle),
                strokeWidth = if (isHourTick) radius * 0.018f else radius * 0.008f,
                cap = StrokeCap.Round,
            )
        }

        drawHourNumbers(center, radius)
        if (state.isHelperTextVisible) {
            drawMinuteHelperNumbers(center, radius)
        }

        if (showHourHand) {
            drawLine(
                color = Color(0xFF1F2430),
                start = center,
                end = ClockMath.pointOnCircle(center, radius * 0.52f, hourAngle),
                strokeWidth = radius * 0.055f,
                cap = StrokeCap.Round,
            )
        }

        if (showMinuteHand) {
            drawLine(
                color = Color(0xFF246BFD),
                start = center,
                end = ClockMath.pointOnCircle(center, radius * 0.76f, minuteAngle),
                strokeWidth = radius * 0.034f,
                cap = StrokeCap.Round,
            )
        }

        drawCircle(Color(0xFFFFB020), radius * 0.06f, center)
        drawCircle(Color.White, radius * 0.025f, center)
    }
}

private fun centerOf(size: IntSize): Offset = Offset(size.width / 2f, size.height / 2f)

private fun pickHand(
    state: ClockState,
    angle: Float,
    offset: Offset,
    center: Offset,
    size: IntSize,
): ClockHand {
    if (state.displayMode == ClockDisplayMode.HOURS_ONLY) return ClockHand.Hour
    if (state.displayMode == ClockDisplayMode.MINUTES_ONLY) return ClockHand.Minute

    val radius = min(size.width, size.height) * 0.46f
    val hourEnd = ClockMath.pointOnCircle(
        center,
        radius * 0.52f,
        ClockMath.hourHandAngleDegrees(state.hours, state.minutes),
    )
    val minuteEnd = ClockMath.pointOnCircle(
        center,
        radius * 0.76f,
        ClockMath.minuteHandAngleDegrees(state.minutes),
    )
    val hourScore = offset.distanceSquaredTo(hourEnd) + angularDistance(
        angle,
        ClockMath.hourHandAngleDegrees(state.hours, state.minutes),
    ) * 200f
    val minuteScore = offset.distanceSquaredTo(minuteEnd) + angularDistance(
        angle,
        ClockMath.minuteHandAngleDegrees(state.minutes),
    ) * 200f
    return if (hourScore < minuteScore) ClockHand.Hour else ClockHand.Minute
}

private fun Offset.distanceSquaredTo(other: Offset): Float {
    val dx = x - other.x
    val dy = y - other.y
    return dx * dx + dy * dy
}

private fun angularDistance(first: Float, second: Float): Float {
    return abs(ClockMath.clockwiseDeltaDegrees(first, second))
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawHourNumbers(
    center: Offset,
    radius: Float,
) {
    val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = android.graphics.Color.rgb(31, 36, 48)
        textAlign = Paint.Align.CENTER
        textSize = radius * 0.14f
        typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD)
    }
    repeat(12) { index ->
        val number = if (index == 0) 12 else index
        val point = ClockMath.pointOnCircle(center, radius * 0.72f, index * 30f)
        drawContext.canvas.nativeCanvas.drawText(
            number.toString(),
            point.x,
            point.y - (paint.descent() + paint.ascent()) / 2f,
            paint,
        )
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawMinuteHelperNumbers(
    center: Offset,
    radius: Float,
) {
    val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = android.graphics.Color.rgb(36, 107, 253)
        textAlign = Paint.Align.CENTER
        textSize = radius * 0.075f
        typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD)
    }
    for (minute in 5..60 step 5) {
        val angle = if (minute == 60) 0f else minute * 6f
        val point = ClockMath.pointOnCircle(center, radius * 1.1f, angle)
        drawContext.canvas.nativeCanvas.drawText(
            minute.toString(),
            point.x,
            point.y - (paint.descent() + paint.ascent()) / 2f,
            paint,
        )
    }
}
