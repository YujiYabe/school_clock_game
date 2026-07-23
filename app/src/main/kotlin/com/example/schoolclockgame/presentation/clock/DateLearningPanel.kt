package com.example.schoolclockgame.presentation.clock

import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.schoolclockgame.domain.calendar.DateMath
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

@Composable
fun DateLearningPanel(
    state: ClockState,
    onDayMove: (Int) -> Unit,
    onMonthMove: (Int) -> Unit,
    onYearMove: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val daysInMonth = DateMath.daysInMonth(state.year, state.month)
    val dayOfYear = DateMath.dayOfYear(state.year, state.month, state.day)
    val daysInYear = DateMath.daysInYear(state.year)

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(28.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier
                .weight(0.42f)
                .fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = state.dateText,
                fontSize = 34.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFF1F2430),
            )
            Text(
                text = "${state.month}がつは ${daysInMonth}にちまで",
                color = Color(0xFF4F5A6B),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
            )

            Spacer(Modifier.height(14.dp))

            YearMonthWheel(
                selectedMonth = state.month,
                yearProgress = DateMath.yearProgress(state.year, state.month, state.day),
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(Modifier.height(12.dp))

            DateStepperRow(label = "にち", onPrevious = { onDayMove(-1) }, onNext = { onDayMove(1) })
            DateStepperRow(label = "つき", onPrevious = { onMonthMove(-1) }, onNext = { onMonthMove(1) })
            DateStepperRow(label = "とし", onPrevious = { onYearMove(-1) }, onNext = { onYearMove(1) })
        }

        Column(
            modifier = Modifier
                .weight(0.58f)
                .fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            MonthDayGrid(
                day = state.day,
                daysInMonth = daysInMonth,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(Modifier.height(18.dp))

            ProgressStrip(
                label = "このつき",
                progress = DateMath.monthProgress(state.year, state.month, state.day),
                value = "${state.day} / $daysInMonth",
            )
            Spacer(Modifier.height(8.dp))
            ProgressStrip(
                label = "このとし",
                progress = dayOfYear.toFloat() / daysInYear,
                value = "$dayOfYear / $daysInYear",
            )
        }
    }
}

@Composable
private fun YearMonthWheel(
    selectedMonth: Int,
    yearProgress: Float,
    modifier: Modifier = Modifier,
) {
    val monthColors = listOf(
        Color(0xFF81C784),
        Color(0xFFFFD54F),
        Color(0xFF4FC3F7),
        Color(0xFFFF8A65),
    )
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = Color.White.copy(alpha = 0.82f),
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(210.dp)
                .padding(16.dp),
        ) {
            val radius = min(size.width, size.height) * 0.42f
            val center = Offset(size.width / 2f, size.height / 2f)
            repeat(12) { index ->
                val month = index + 1
                drawArc(
                    color = monthColors[index / 3].copy(alpha = if (month == selectedMonth) 0.95f else 0.32f),
                    startAngle = index * 30f - 90f,
                    sweepAngle = 27f,
                    useCenter = false,
                    topLeft = Offset(center.x - radius, center.y - radius),
                    size = Size(radius * 2f, radius * 2f),
                    style = Stroke(width = radius * 0.22f, cap = StrokeCap.Butt),
                )
            }
            drawArc(
                color = Color(0xFF246BFD),
                startAngle = -90f,
                sweepAngle = 360f * yearProgress.coerceIn(0f, 1f),
                useCenter = false,
                topLeft = Offset(center.x - radius * 0.62f, center.y - radius * 0.62f),
                size = Size(radius * 1.24f, radius * 1.24f),
                style = Stroke(width = radius * 0.05f, cap = StrokeCap.Round),
            )
            drawContext.canvas.nativeCanvas.apply {
                val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = android.graphics.Color.rgb(31, 36, 48)
                    textAlign = Paint.Align.CENTER
                    textSize = radius * 0.14f
                    typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD)
                }
                repeat(12) { index ->
                    val point = pointOnCircle(center, radius, index * 30f + 15f)
                    drawText(
                        "${index + 1}",
                        point.x,
                        point.y - (paint.descent() + paint.ascent()) / 2f,
                        paint,
                    )
                }
                val centerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = android.graphics.Color.rgb(31, 36, 48)
                    textAlign = Paint.Align.CENTER
                    textSize = radius * 0.16f
                    typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD)
                }
                drawText("12かげつ", center.x, center.y - (centerPaint.descent() + centerPaint.ascent()) / 2f, centerPaint)
            }
        }
    }
}

@Composable
@OptIn(ExperimentalLayoutApi::class)
private fun MonthDayGrid(
    day: Int,
    daysInMonth: Int,
    modifier: Modifier = Modifier,
) {
    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalArrangement = Arrangement.spacedBy(6.dp),
        maxItemsInEachRow = 7,
    ) {
        repeat(daysInMonth) { index ->
            val date = index + 1
            val isPastOrToday = date <= day
            val isToday = date == day
            Box(
                modifier = Modifier
                    .padding(horizontal = 3.dp)
                    .size(34.dp)
                    .background(
                        color = when {
                            isToday -> Color(0xFFFFB020)
                            isPastOrToday -> Color(0xFF4FC3F7)
                            else -> Color.White.copy(alpha = 0.9f)
                        },
                        shape = RoundedCornerShape(7.dp),
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = date.toString(),
                    color = if (isPastOrToday) Color(0xFF111827) else Color(0xFF6B7280),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

@Composable
private fun ProgressStrip(
    label: String,
    progress: Float,
    value: String,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(label, fontWeight = FontWeight.Bold, color = Color(0xFF1F2430))
            Text(value, fontWeight = FontWeight.Bold, color = Color(0xFF4F5A6B))
        }
        LinearProgressIndicator(
            progress = { progress.coerceIn(0f, 1f) },
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp),
            color = Color(0xFF246BFD),
            trackColor = Color.White.copy(alpha = 0.92f),
        )
    }
}

@Composable
private fun DateStepperRow(
    label: String,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        OutlinedButton(onClick = onPrevious, shape = RoundedCornerShape(8.dp)) {
            Text("-")
        }
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 16.dp),
            fontWeight = FontWeight.Black,
            style = MaterialTheme.typography.titleMedium,
        )
        Button(onClick = onNext, shape = RoundedCornerShape(8.dp)) {
            Text("+")
        }
    }
}

private fun pointOnCircle(center: Offset, radius: Float, degrees: Float): Offset {
    val radians = (degrees - 90f) * PI.toFloat() / 180f
    return Offset(
        x = center.x + cos(radians) * radius,
        y = center.y + sin(radians) * radius,
    )
}
