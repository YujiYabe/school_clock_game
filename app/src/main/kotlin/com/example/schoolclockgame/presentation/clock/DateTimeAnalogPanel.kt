package com.example.schoolclockgame.presentation.clock

import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
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
    onDialFocus: (ExpandedDial) -> Unit,
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
            spec = DialSpec(
                currentValue = state.month,
                count = 12,
                centerText = "${state.month}がつ",
                color = Color(0xFF18A058),
                onValueChange = { target -> moveDialValue(state.month, 12, target, onMonthMove) },
                onStep = onMonthMove,
                onFocus = { onDialFocus(ExpandedDial.Month) },
            ),
            modifier = Modifier.weight(1f),
        )
        UnitDial(
            spec = DialSpec(
                currentValue = state.day,
                count = daysInMonth,
                centerText = "${state.day}にち",
                color = Color(0xFFFFB020),
                onValueChange = { target -> moveDialValue(state.day, daysInMonth, target, onDayMove) },
                onStep = onDayMove,
                onFocus = { onDialFocus(ExpandedDial.Day) },
            ),
            modifier = Modifier.weight(1f),
        )
        UnitDial(
            spec = DialSpec(
                currentValue = state.hours,
                count = 24,
                centerText = "%02dじ".format(state.hours),
                color = Color(0xFF246BFD),
                onValueChange = onHourSelected,
                onStep = onHourMove,
                valueOffset = 0,
                onFocus = { onDialFocus(ExpandedDial.Hour) },
            ),
            modifier = Modifier.weight(1f),
        )
        UnitDial(
            spec = DialSpec(
                currentValue = state.minutes,
                count = 60,
                centerText = "%02dふん".format(state.minutes),
                color = Color(0xFF7C3AED),
                onValueChange = onMinuteSelected,
                onStep = onMinuteMove,
                labelStep = 10,
                valueOffset = 0,
                onFocus = { onDialFocus(ExpandedDial.Minute) },
            ),
            modifier = Modifier.weight(1f),
        )
        UnitDial(
            spec = DialSpec(
                currentValue = state.seconds,
                count = 60,
                centerText = "%02dびょう".format(state.seconds),
                color = Color(0xFFEF4444),
                onValueChange = onSecondSelected,
                onStep = onSecondMove,
                labelStep = 10,
                valueOffset = 0,
                onFocus = { onDialFocus(ExpandedDial.Second) },
            ),
            modifier = Modifier.weight(1f),
        )
    }
}

enum class ExpandedDial {
    Month,
    Day,
    Hour,
    Minute,
    Second,
}

@Composable
fun DateTimeDialOverlay(
    dial: ExpandedDial,
    state: ClockState,
    onDismiss: () -> Unit,
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
    val expandedSpec = dial.spec(
        state = state,
        daysInMonth = daysInMonth,
        onMonthMove = onMonthMove,
        onDayMove = onDayMove,
        onHourMove = onHourMove,
        onMinuteMove = onMinuteMove,
        onSecondMove = onSecondMove,
        onMonthSelected = onMonthSelected,
        onDaySelected = onDaySelected,
        onHourSelected = onHourSelected,
        onMinuteSelected = onMinuteSelected,
        onSecondSelected = onSecondSelected,
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White.copy(alpha = 0.62f))
            .pointerInput(Unit) {
                detectTapGestures { onDismiss() }
            },
        contentAlignment = Alignment.Center,
    ) {
        UnitDial(
            spec = expandedSpec,
            modifier = Modifier
                .fillMaxWidth(0.63f)
                .fillMaxHeight(0.96f),
            showAllLabels = true,
            expanded = true,
            showStepButtons = false,
        )
    }
}

private data class DialSpec(
    val currentValue: Int,
    val count: Int,
    val centerText: String,
    val color: Color,
    val onValueChange: (Int) -> Unit,
    val onStep: (Int) -> Unit,
    val labelStep: Int = 1,
    val valueOffset: Int = 1,
    val onFocus: () -> Unit = {},
)

private fun ExpandedDial.spec(
    state: ClockState,
    daysInMonth: Int,
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
): DialSpec {
    return when (this) {
        ExpandedDial.Month -> DialSpec(
            currentValue = state.month,
            count = 12,
            centerText = "${state.month}がつ",
            color = Color(0xFF18A058),
            onValueChange = onMonthSelected,
            onStep = onMonthMove,
        )

        ExpandedDial.Day -> DialSpec(
            currentValue = state.day,
            count = daysInMonth,
            centerText = "${state.day}にち",
            color = Color(0xFFFFB020),
            onValueChange = onDaySelected,
            onStep = onDayMove,
        )

        ExpandedDial.Hour -> DialSpec(
            currentValue = state.hours,
            count = 24,
            centerText = "%02dじ".format(state.hours),
            color = Color(0xFF246BFD),
            onValueChange = onHourSelected,
            onStep = onHourMove,
            valueOffset = 0,
        )

        ExpandedDial.Minute -> DialSpec(
            currentValue = state.minutes,
            count = 60,
            centerText = "%02dふん".format(state.minutes),
            color = Color(0xFF7C3AED),
            onValueChange = onMinuteSelected,
            onStep = onMinuteMove,
            valueOffset = 0,
        )

        ExpandedDial.Second -> DialSpec(
            currentValue = state.seconds,
            count = 60,
            centerText = "%02dびょう".format(state.seconds),
            color = Color(0xFFEF4444),
            onValueChange = onSecondSelected,
            onStep = onSecondMove,
            valueOffset = 0,
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
            (-2..2).forEach { offset ->
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
    spec: DialSpec,
    modifier: Modifier = Modifier,
    showAllLabels: Boolean = false,
    expanded: Boolean = false,
    showStepButtons: Boolean = true,
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
            if (showStepButtons) {
                AnalogStepButton(text = "▲", onClick = { spec.onStep(1) })
            }
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
                        .pointerInput(spec.count, spec.valueOffset, expanded) {
                            detectTapGestures { offset ->
                                spec.onFocus()
                                if (expanded) {
                                    spec.onValueChange(
                                        pointToDialValue(
                                            offset,
                                            size.width.toFloat(),
                                            size.height.toFloat(),
                                            spec.count,
                                            spec.valueOffset,
                                        ),
                                    )
                                }
                            }
                        }
                        .pointerInput(spec.count, spec.valueOffset) {
                            detectDragGestures(
                                onDragStart = { offset ->
                                    spec.onValueChange(
                                        pointToDialValue(
                                            offset,
                                            size.width.toFloat(),
                                            size.height.toFloat(),
                                            spec.count,
                                            spec.valueOffset,
                                        ),
                                    )
                                },
                                onDrag = { change, _ ->
                                    spec.onValueChange(
                                        pointToDialValue(
                                            change.position,
                                            size.width.toFloat(),
                                            size.height.toFloat(),
                                            spec.count,
                                            spec.valueOffset,
                                        ),
                                    )
                                    change.consume()
                                },
                            )
                        },
                ) {
                    val radius = min(size.width, size.height) * 0.43f
                    val center = Offset(size.width / 2f, size.height / 2f)
                    val selected = spec.currentValue.coerceIn(spec.valueOffset, spec.valueOffset + spec.count - 1)
                    val sweep = selected.toFloat() / spec.count.toFloat() * 360f

                    drawCircle(Color(0xFFF7F8FB), radius, center)
                    drawArc(
                        color = spec.color.copy(alpha = 0.2f),
                        startAngle = -90f,
                        sweepAngle = sweep,
                        useCenter = true,
                        topLeft = Offset(center.x - radius, center.y - radius),
                        size = Size(radius * 2f, radius * 2f),
                    )
                    drawCircle(Color(0xFF2F3645), radius, center, style = Stroke(width = radius * 0.025f))

                    repeat(spec.count) { index ->
                        val value = spec.valueOffset + index
                        val isLabeled = showAllLabels || shouldShowDialLabel(value, spec.count, spec.labelStep)
                        val angle = valueToClockFaceAngle(value, spec.count)
                        val startRadius = if (isLabeled) radius * 0.84f else radius * 0.91f
                        drawLine(
                            color = if (isLabeled) Color(0xFF1F2430) else Color(0xFFB7BECA),
                            start = pointOnCircle(center, startRadius, angle),
                            end = pointOnCircle(center, radius * 0.98f, angle),
                            strokeWidth = if (isLabeled) radius * 0.016f else radius * 0.007f,
                            cap = StrokeCap.Round,
                        )
                    }

                    drawLabels(
                        center = center,
                        radius = radius,
                        count = spec.count,
                        labelStep = spec.labelStep,
                        selected = selected,
                        valueOffset = spec.valueOffset,
                        showAllLabels = showAllLabels,
                    )

                    val handAngle = valueToClockFaceAngle(selected, spec.count)
                    drawLine(
                        color = spec.color,
                        start = center,
                        end = pointOnCircle(center, radius * 0.68f, handAngle),
                        strokeWidth = radius * 0.038f,
                        cap = StrokeCap.Round,
                    )
                    drawCircle(spec.color, radius * 0.055f, center)
                }

                Text(
                    text = spec.centerText,
                    fontSize = if (expanded) 26.sp else 17.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF1F2430),
                )
            }
            if (showStepButtons) {
                AnalogStepButton(text = "▼", onClick = { spec.onStep(-1) })
            }
        }
    }
}

@Composable
private fun AnalogStepButton(
    text: String,
    onClick: () -> Unit,
) {
    RepeatingTextButton(
        text = text,
        onPress = onClick,
        fontSize = 22.sp,
    )
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
    valueOffset: Int,
    showAllLabels: Boolean,
) {
    val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = android.graphics.Color.rgb(31, 36, 48)
        textAlign = Paint.Align.CENTER
        textSize = when {
            count > 31 && showAllLabels -> radius * 0.052f
            count > 31 -> radius * 0.072f
            else -> radius * 0.105f
        }
        typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD)
    }
    val selectedPaint = Paint(paint).apply {
        color = android.graphics.Color.rgb(255, 176, 32)
        textSize = paint.textSize * 1.16f
    }
    repeat(count) { index ->
        val value = valueOffset + index
        if (showAllLabels || shouldShowDialLabel(value, count, labelStep) || value == selected) {
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

private fun pointToDialValue(
    point: Offset,
    width: Float,
    height: Float,
    count: Int,
    valueOffset: Int,
): Int {
    val center = Offset(width / 2f, height / 2f)
    val angle = pointToClockAngleDegrees(point, center)
    val index = (angle / 360f * count).roundToInt().floorMod(count)
    if (valueOffset == 1) {
        return if (index == 0) count else index
    }
    return valueOffset + index
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
    return value.floorMod(count) * 360f / count
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
