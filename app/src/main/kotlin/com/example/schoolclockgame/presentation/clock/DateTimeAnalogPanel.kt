package com.example.schoolclockgame.presentation.clock

import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.schoolclockgame.domain.calendar.DateMath
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sin

@Composable
fun DateTimeAnalogPanel(
    state: ClockState,
    onYearMove: (Int) -> Unit,
    onMonthMove: (Int) -> Unit,
    onDayMove: (Int) -> Unit,
    onHourMove: (Int) -> Unit,
    onMinuteMove: (Int) -> Unit,
    onSecondMove: (Int) -> Unit,
    onMonthSelected: (Int) -> Unit,
    onDaySelected: (Int) -> Unit,
    onHourSelected: (Int) -> Unit,
    onMinuteSelected: (Int) -> Unit,
    onSecondSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val daysInMonth = DateMath.daysInMonth(state.year, state.month)

    Row(
        modifier = modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        YearAnalogPanel(
            year = state.year,
            onYearMove = onYearMove,
            modifier = Modifier
                .weight(1.25f)
                .fillMaxHeight(),
        )
        UnitDial(
            currentValue = state.month,
            count = 12,
            centerText = "${state.month}がつ",
            color = Color(0xFF18A058),
            onValueChange = { target -> moveDialValue(state.month, 12, target, onMonthMove) },
            onStep = onMonthMove,
            modifier = Modifier.weight(1f),
        )
        UnitDial(
            currentValue = state.day,
            count = daysInMonth,
            centerText = "${state.day}にち",
            color = Color(0xFFFFB020),
            onValueChange = { target -> moveDialValue(state.day, daysInMonth, target, onDayMove) },
            onStep = onDayMove,
            modifier = Modifier.weight(1f),
        )
        val hourDialValue = if (state.hours == 0) 24 else state.hours
        UnitDial(
            currentValue = hourDialValue,
            count = 24,
            centerText = "%02dじ".format(state.hours),
            color = Color(0xFF246BFD),
            onValueChange = { target -> moveDialValue(hourDialValue, 24, target, onHourMove) },
            onStep = onHourMove,
            modifier = Modifier.weight(1f),
        )
        val minuteDialValue = state.minutes + 1
        UnitDial(
            currentValue = minuteDialValue,
            count = 60,
            centerText = "%02dふん".format(state.minutes),
            color = Color(0xFF7C3AED),
            onValueChange = { target -> moveDialValue(minuteDialValue, 60, target, onMinuteMove) },
            onStep = onMinuteMove,
            modifier = Modifier.weight(1f),
            labelStep = 10,
        )
        val secondDialValue = state.seconds + 1
        UnitDial(
            currentValue = secondDialValue,
            count = 60,
            centerText = "%02dびょう".format(state.seconds),
            color = Color(0xFFEF4444),
            onValueChange = { target -> moveDialValue(secondDialValue, 60, target, onSecondMove) },
            onStep = onSecondMove,
            modifier = Modifier.weight(1f),
            labelStep = 10,
        )
    }
}

@Composable
private fun YearAnalogPanel(
    year: Int,
    onYearMove: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.pointerInput(year) {
            var accumulatedDrag = 0f
            detectDragGestures(
                onDragStart = { accumulatedDrag = 0f },
                onDrag = { change, dragAmount ->
                    accumulatedDrag += dragAmount.y
                    when {
                        accumulatedDrag <= -32f -> {
                            onYearMove(1)
                            accumulatedDrag = 0f
                        }

                        accumulatedDrag >= 32f -> {
                            onYearMove(-1)
                            accumulatedDrag = 0f
                        }
                    }
                    change.consume()
                },
            )
        },
        shape = RoundedCornerShape(8.dp),
        color = Color.White.copy(alpha = 0.86f),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp, vertical = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly,
        ) {
            AnalogStepButton(text = "▲", onClick = { onYearMove(1) })
            (-3..3).forEach { offset ->
                YearSelectorText(
                    year = year + offset,
                    selected = offset == 0,
                )
            }
            AnalogStepButton(text = "▼", onClick = { onYearMove(-1) })
        }
    }
}

@Composable
private fun YearSelectorText(
    year: Int,
    selected: Boolean,
) {
    Text(
        text = year.toString(),
        fontSize = if (selected) 30.sp else 15.sp,
        fontWeight = if (selected) FontWeight.Black else FontWeight.Normal,
        color = if (selected) Color(0xFF1A73E8) else Color(0xFF4F4F4F),
        maxLines = 1,
    )
}

@Composable
private fun UnitDial(
    currentValue: Int,
    count: Int,
    centerText: String,
    color: Color,
    onValueChange: (Int) -> Unit,
    onStep: (Int) -> Unit,
    modifier: Modifier = Modifier,
    labelStep: Int = 1,
) {
    Surface(
        modifier = modifier.fillMaxHeight(),
        shape = RoundedCornerShape(8.dp),
        color = Color.White.copy(alpha = 0.88f),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            AnalogStepButton(text = "▲", onClick = { onStep(1) })
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center,
            ) {
                Canvas(
                    modifier = Modifier
                        .fillMaxHeight()
                        .aspectRatio(1f)
                        .pointerInput(count) {
                            detectDragGestures(
                                onDragStart = { offset ->
                                    onValueChange(pointToOneBasedValue(offset, size.width.toFloat(), size.height.toFloat(), count))
                                },
                                onDrag = { change, _ ->
                                    onValueChange(pointToOneBasedValue(change.position, size.width.toFloat(), size.height.toFloat(), count))
                                    change.consume()
                                },
                            )
                        },
                ) {
                    val radius = min(size.width, size.height) * 0.43f
                    val center = Offset(size.width / 2f, size.height / 2f)
                    val selected = currentValue.coerceIn(1, count)
                    val sweep = selected.toFloat() / count.toFloat() * 360f

                    drawCircle(Color(0xFFF7F8FB), radius, center)
                    drawArc(
                        color = color.copy(alpha = 0.2f),
                        startAngle = -90f,
                        sweepAngle = sweep,
                        useCenter = true,
                        topLeft = Offset(center.x - radius, center.y - radius),
                        size = Size(radius * 2f, radius * 2f),
                    )
                    drawCircle(Color(0xFF2F3645), radius, center, style = Stroke(width = radius * 0.025f))

                    repeat(count) { index ->
                        val value = index + 1
                        val isLabeled = shouldShowDialLabel(value, count, labelStep)
                        val angle = valueToClockFaceAngle(value, count)
                        val startRadius = if (isLabeled) radius * 0.84f else radius * 0.91f
                        drawLine(
                            color = if (isLabeled) Color(0xFF1F2430) else Color(0xFFB7BECA),
                            start = pointOnCircle(center, startRadius, angle),
                            end = pointOnCircle(center, radius * 0.98f, angle),
                            strokeWidth = if (isLabeled) radius * 0.016f else radius * 0.007f,
                            cap = StrokeCap.Round,
                        )
                    }

                    val handAngle = valueToClockFaceAngle(selected, count)
                    drawLine(
                        color = color,
                        start = center,
                        end = pointOnCircle(center, radius * 0.68f, handAngle),
                        strokeWidth = radius * 0.038f,
                        cap = StrokeCap.Round,
                    )
                    drawCircle(color, radius * 0.055f, center)

                    drawLabels(
                        center = center,
                        radius = radius,
                        count = count,
                        labelStep = labelStep,
                        selected = selected,
                    )
                }

                Text(
                    text = centerText,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF1F2430),
                )
            }
            AnalogStepButton(text = "▼", onClick = { onStep(-1) })
        }
    }
}

@Composable
private fun AnalogStepButton(
    text: String,
    onClick: () -> Unit,
) {
    IconButton(onClick = onClick) {
        Text(
            text = text,
            fontSize = 22.sp,
            fontWeight = FontWeight.Black,
            color = Color(0xFF246BFD),
        )
    }
}

@Composable
private fun RelationPanel(
    state: ClockState,
    daysInMonth: Int,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxHeight(),
        shape = RoundedCornerShape(8.dp),
        color = Color.White.copy(alpha = 0.84f),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text("まとまり", fontSize = 24.sp, fontWeight = FontWeight.Black, color = Color(0xFF1F2430))
            Spacer(Modifier.height(12.dp))
            RelationLine("1年", "12か月")
            RelationLine("1か月", "${daysInMonth}日")
            RelationLine("1日", "24時間")
            RelationLine("1時間", "60分")
            RelationLine("1分", "60秒")
            Spacer(Modifier.height(12.dp))
            Text(
                text = if (DateMath.isLeapYear(state.year)) "うるうどし" else "ふつうのとし",
                color = Color(0xFF4F5A6B),
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
private fun RelationLine(
    left: String,
    right: String,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(left, fontWeight = FontWeight.Bold, color = Color(0xFF1F2430))
        Text(right, fontWeight = FontWeight.Black, color = Color(0xFF246BFD))
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawLabels(
    center: Offset,
    radius: Float,
    count: Int,
    labelStep: Int,
    selected: Int,
) {
    val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = android.graphics.Color.rgb(31, 36, 48)
        textAlign = Paint.Align.CENTER
        textSize = if (count > 31) radius * 0.072f else radius * 0.105f
        typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD)
    }
    val selectedPaint = Paint(paint).apply {
        color = android.graphics.Color.rgb(255, 176, 32)
        textSize = paint.textSize * 1.16f
    }
    repeat(count) { index ->
        val value = index + 1
        if (shouldShowDialLabel(value, count, labelStep) || value == selected) {
            val point = pointOnCircle(center, radius * 0.72f, valueToClockFaceAngle(value, count))
            val activePaint = if (value == selected) selectedPaint else paint
            drawContext.canvas.nativeCanvas.drawText(
                value.toString(),
                point.x,
                point.y - (activePaint.descent() + activePaint.ascent()) / 2f,
                activePaint,
            )
        }
    }
}

private fun pointToOneBasedValue(
    point: Offset,
    width: Float,
    height: Float,
    count: Int,
): Int {
    val center = Offset(width / 2f, height / 2f)
    val angle = pointToClockAngleDegrees(point, center)
    val value = (angle / 360f * count).roundToInt().floorMod(count)
    return if (value == 0) count else value
}

private fun moveDialValue(
    currentValue: Int,
    count: Int,
    targetValue: Int,
    onMove: (Int) -> Unit,
) {
    val currentIndex = currentValue.coerceIn(1, count) - 1
    val targetIndex = targetValue.coerceIn(1, count) - 1
    var delta = targetIndex - currentIndex
    if (delta > count / 2) delta -= count
    if (delta < -count / 2) delta += count
    if (delta != 0) onMove(delta)
}

private fun valueToClockFaceAngle(value: Int, count: Int): Float {
    return value.coerceIn(1, count) * 360f / count
}

private fun shouldShowDialLabel(value: Int, count: Int, labelStep: Int): Boolean {
    val showOne = count < 60
    return value == count || (showOne && value == 1) || value % labelStep == 0
}

private fun pointToClockAngleDegrees(point: Offset, center: Offset): Float {
    val radians = atan2(point.y - center.y, point.x - center.x)
    val degreesFromThreeOClock = radians * 180f / PI.toFloat()
    return (degreesFromThreeOClock + 90f).floorMod(360f)
}

private fun pointOnCircle(center: Offset, radius: Float, degrees: Float): Offset {
    val radians = (degrees - 90f) * PI.toFloat() / 180f
    return Offset(
        x = center.x + cos(radians) * radius,
        y = center.y + sin(radians) * radius,
    )
}

private fun Float.floorMod(mod: Float): Float {
    val result = this % mod
    return if (result < 0f) result + mod else result
}

private fun Int.floorMod(mod: Int): Int {
    val result = this % mod
    return if (result < 0) result + mod else result
}
