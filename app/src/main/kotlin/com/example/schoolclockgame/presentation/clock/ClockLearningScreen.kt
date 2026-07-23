package com.example.schoolclockgame.presentation.clock

import androidx.compose.foundation.background
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.inset
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.schoolclockgame.ui.theme.SchoolClockTheme

@Composable
fun ClockLearningScreen(
    viewModel: ClockViewModel = viewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ClockLearningContent(
        state = state,
        onYearMove = viewModel::moveYear,
        onMonthMove = viewModel::moveMonth,
        onDayMove = viewModel::moveDay,
        onHourMove = viewModel::moveHour,
        onMinuteMove = viewModel::moveMinute,
        onSecondMove = viewModel::moveSecond,
        onResumeClock = viewModel::resumeCurrentTime,
        onPauseClock = viewModel::pauseClock,
        onYearSelected = viewModel::setYear,
        onMonthSelected = viewModel::setMonth,
        onDaySelected = viewModel::setDay,
        onHourValueSelected = viewModel::setHourValue,
        onMinuteValueSelected = viewModel::setMinuteValue,
        onSecondValueSelected = viewModel::setSecondValue,
        onHourSelected = viewModel::setHourFromOneBasedDial,
        onMinuteSelected = viewModel::setMinuteFromOneBasedDial,
        onSecondSelected = viewModel::setSecondFromOneBasedDial,
    )
}

@Composable
fun ClockLearningContent(
    state: ClockState,
    onYearMove: (Int) -> Unit,
    onMonthMove: (Int) -> Unit,
    onDayMove: (Int) -> Unit,
    onHourMove: (Int) -> Unit,
    onMinuteMove: (Int) -> Unit,
    onSecondMove: (Int) -> Unit,
    onResumeClock: () -> Unit,
    onPauseClock: () -> Unit,
    onYearSelected: (Int) -> Unit,
    onMonthSelected: (Int) -> Unit,
    onDaySelected: (Int) -> Unit,
    onHourValueSelected: (Int) -> Unit,
    onMinuteValueSelected: (Int) -> Unit,
    onSecondValueSelected: (Int) -> Unit,
    onHourSelected: (Int) -> Unit,
    onMinuteSelected: (Int) -> Unit,
    onSecondSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = rememberTimeContext(state.hours)
    val background = Color(0xFFF4F8F2)

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(background),
        containerColor = background,
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 28.dp, vertical = 22.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            ClockModeHeader(
                isLiveClockRunning = state.isLiveClockRunning,
                onResumeClock = onResumeClock,
                onPauseClock = onPauseClock,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
            )

            DigitalDateTimeRow(
                state = state,
                icon = context.icon,
                onYearMove = onYearMove,
                onMonthMove = onMonthMove,
                onDayMove = onDayMove,
                onHourMove = onHourMove,
                onMinuteMove = onMinuteMove,
                onSecondMove = onSecondMove,
                onYearSelected = onYearSelected,
                onMonthSelected = onMonthSelected,
                onDaySelected = onDaySelected,
                onHourValueSelected = onHourValueSelected,
                onMinuteValueSelected = onMinuteValueSelected,
                onSecondValueSelected = onSecondValueSelected,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
            )

            UnitLabelRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
            )

            DateTimeAnalogPanel(
                state = state,
                onYearMove = onYearMove,
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
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
            )
        }
    }
}

private data class TimeContext(
    val icon: String,
)

@Composable
private fun ClockModeHeader(
    isLiveClockRunning: Boolean,
    onResumeClock: () -> Unit,
    onPauseClock: () -> Unit,
    modifier: Modifier = Modifier,
) {
    SingleChoiceSegmentedButtonRow(modifier = modifier) {
        SegmentedButton(
            selected = isLiveClockRunning,
            onClick = onResumeClock,
            shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
        ) {
            Text("現在値", fontWeight = FontWeight.Black)
        }
        SegmentedButton(
            selected = !isLiveClockRunning,
            onClick = onPauseClock,
            shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
        ) {
            Text("停止", fontWeight = FontWeight.Black)
        }
    }
}

@Composable
private fun DigitalDateTimeRow(
    state: ClockState,
    icon: String,
    onYearMove: (Int) -> Unit,
    onMonthMove: (Int) -> Unit,
    onDayMove: (Int) -> Unit,
    onHourMove: (Int) -> Unit,
    onMinuteMove: (Int) -> Unit,
    onSecondMove: (Int) -> Unit,
    onYearSelected: (Int) -> Unit,
    onMonthSelected: (Int) -> Unit,
    onDaySelected: (Int) -> Unit,
    onHourValueSelected: (Int) -> Unit,
    onMinuteValueSelected: (Int) -> Unit,
    onSecondValueSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxHeight(),
        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        DigitalUnit(
            value = state.year.toString(),
            initialAmount = "",
            onMove = onYearMove,
            onSetValue = onYearSelected,
            isSetValueValid = { it in 1900..2100 },
            modifier = Modifier.weight(1.25f),
        )
        DigitalUnit(
            value = "%02d".format(state.month),
            initialAmount = "",
            onMove = onMonthMove,
            onSetValue = onMonthSelected,
            isSetValueValid = { it in 1..12 },
            modifier = Modifier.weight(1f),
        )
        DigitalUnit(
            value = "%02d".format(state.day),
            initialAmount = "",
            onMove = onDayMove,
            onSetValue = onDaySelected,
            isSetValueValid = { it in 1..com.example.schoolclockgame.domain.calendar.DateMath.daysInMonth(state.year, state.month) },
            modifier = Modifier.weight(1f),
        )
        DigitalUnit(
            value = "%02d".format(state.hours),
            initialAmount = "",
            onMove = onHourMove,
            onSetValue = onHourValueSelected,
            isSetValueValid = { it in 0..23 },
            modifier = Modifier.weight(1f),
        )
        DigitalUnit(
            value = "%02d".format(state.minutes),
            initialAmount = "",
            onMove = onMinuteMove,
            onSetValue = onMinuteValueSelected,
            isSetValueValid = { it in 0..59 },
            modifier = Modifier.weight(1f),
        )
        DigitalUnit(
            value = "%02d".format(state.seconds),
            initialAmount = "",
            onMove = onSecondMove,
            onSetValue = onSecondValueSelected,
            isSetValueValid = { it in 0..59 },
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun UnitLabelRow(
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        UnitLabel("年", Modifier.weight(1.25f))
        UnitLabel("月", Modifier.weight(1f))
        UnitLabel("日", Modifier.weight(1f))
        UnitLabel("時", Modifier.weight(1f))
        UnitLabel("分", Modifier.weight(1f))
        UnitLabel("秒", Modifier.weight(1f))
    }
}

@Composable
private fun UnitLabel(
    label: String,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxHeight(),
        shape = RoundedCornerShape(8.dp),
        color = Color(0xFF111827),
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center,
        ) {
            Text(
                text = label,
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                color = Color.White,
                maxLines = 1,
            )
        }
    }
}

@Composable
private fun DigitalUnit(
    value: String,
    initialAmount: String,
    onMove: (Int) -> Unit,
    onSetValue: (Int) -> Unit,
    isSetValueValid: (Int) -> Boolean,
    modifier: Modifier = Modifier,
) {
    var amountText by remember { mutableStateOf(initialAmount) }
    val parsedAmount = amountText.toIntOrNull()
    val amount = parsedAmount?.coerceIn(0, 999_999) ?: 0

    Surface(
        modifier = modifier.fillMaxHeight(),
        shape = RoundedCornerShape(8.dp),
        color = Color.White.copy(alpha = 0.88f),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 8.dp, horizontal = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center,
        ) {
                Text(
                    text = value,
                    fontSize = 44.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF1F2430),
                    maxLines = 1,
            )
            AmountPad(
                amountText = amountText,
                onAmountTextChange = { amountText = it },
                onDecreaseAmount = {
                    amountText = (amount - 1).coerceAtLeast(0).toString()
                },
                onIncreaseAmount = {
                    amountText = ((parsedAmount ?: -1) + 1).coerceIn(0, 999_999).toString()
                },
                onMove = onMove,
                onClearAmount = { amountText = "" },
                onSetValue = { parsedAmount?.let(onSetValue) },
                canSetValue = parsedAmount?.let(isSetValueValid) == true,
                amount = amount,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun AmountPad(
    amountText: String,
    onAmountTextChange: (String) -> Unit,
    onDecreaseAmount: () -> Unit,
    onIncreaseAmount: () -> Unit,
    onMove: (Int) -> Unit,
    onClearAmount: () -> Unit,
    onSetValue: () -> Unit,
    canSetValue: Boolean,
    amount: Int,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            DigitalStepButton(
                text = "▲",
                onClick = onIncreaseAmount,
                enabled = amount < 999_999,
                modifier = Modifier.align(Alignment.Center),
            )
            DigitalCornerButton(
                icon = DigitalCornerIcon.Clear,
                onClick = onClearAmount,
                modifier = Modifier.align(Alignment.CenterEnd),
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            DigitalStepButton(text = "-", onClick = { onMove(-amount) }, enabled = amount > 0)
            OutlinedTextField(
                value = amountText,
                onValueChange = { next ->
                    onAmountTextChange(next.filter { it.isDigit() }.take(6))
                },
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp),
                singleLine = true,
                textStyle = androidx.compose.ui.text.TextStyle(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center,
                ),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.NumberPassword,
                    imeAction = ImeAction.Done,
                ),
            )
            DigitalStepButton(text = "+", onClick = { onMove(amount) }, enabled = amount > 0)
        }
        Box(modifier = Modifier.fillMaxWidth()) {
            DigitalStepButton(
                text = "▼",
                onClick = onDecreaseAmount,
                enabled = amount > 0,
                modifier = Modifier.align(Alignment.Center),
            )
            DigitalCornerButton(
                icon = DigitalCornerIcon.Set,
                onClick = onSetValue,
                enabled = canSetValue,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .alpha(if (canSetValue) 1f else 0f),
            )
        }
    }
}

private enum class DigitalCornerIcon {
    Clear,
    Set,
}

@Composable
private fun DigitalCornerButton(
    icon: DigitalCornerIcon,
    onClick: () -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
) {
    IconButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier,
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            inset(size.width * 0.1f, size.height * 0.1f) {
            val strokeColor = Color(0xFF123C6B)
            val strokeWidth = size.minDimension * 0.065f

            when (icon) {
                DigitalCornerIcon.Clear -> {
                    val path = Path().apply {
                        moveTo(size.width * 0.34f, size.height * 0.25f)
                        lineTo(size.width * 0.82f, size.height * 0.25f)
                        lineTo(size.width * 0.94f, size.height * 0.50f)
                        lineTo(size.width * 0.82f, size.height * 0.75f)
                        lineTo(size.width * 0.34f, size.height * 0.75f)
                        lineTo(size.width * 0.10f, size.height * 0.50f)
                        close()
                    }
                    drawPath(
                        path = path,
                        color = strokeColor,
                        style = Stroke(width = strokeWidth),
                    )
                    drawLine(
                        color = strokeColor,
                        start = androidx.compose.ui.geometry.Offset(size.width * 0.46f, size.height * 0.40f),
                        end = androidx.compose.ui.geometry.Offset(size.width * 0.64f, size.height * 0.60f),
                        strokeWidth = strokeWidth,
                        cap = StrokeCap.Round,
                    )
                    drawLine(
                        color = strokeColor,
                        start = androidx.compose.ui.geometry.Offset(size.width * 0.64f, size.height * 0.40f),
                        end = androidx.compose.ui.geometry.Offset(size.width * 0.46f, size.height * 0.60f),
                        strokeWidth = strokeWidth,
                        cap = StrokeCap.Round,
                    )
                }

                DigitalCornerIcon.Set -> {
                    drawCircle(
                        color = strokeColor,
                        radius = size.minDimension * 0.32f,
                        center = center,
                        style = Stroke(width = strokeWidth),
                    )
                    drawLine(
                        color = strokeColor,
                        start = androidx.compose.ui.geometry.Offset(size.width * 0.36f, size.height * 0.52f),
                        end = androidx.compose.ui.geometry.Offset(size.width * 0.48f, size.height * 0.64f),
                        strokeWidth = strokeWidth,
                        cap = StrokeCap.Round,
                    )
                    drawLine(
                        color = strokeColor,
                        start = androidx.compose.ui.geometry.Offset(size.width * 0.48f, size.height * 0.64f),
                        end = androidx.compose.ui.geometry.Offset(size.width * 0.68f, size.height * 0.38f),
                        strokeWidth = strokeWidth,
                        cap = StrokeCap.Round,
                    )
                }
            }
            }
        }
    }
}

@Composable
private fun DigitalStepButton(
    text: String,
    onClick: () -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier,
) {
    IconButton(onClick = onClick, enabled = enabled, modifier = modifier) {
        Text(
            text = text,
            fontSize = 24.sp,
            fontWeight = FontWeight.Black,
            color = if (enabled) Color(0xFF246BFD) else Color(0xFFB7BECA),
        )
    }
}

@Composable
private fun rememberTimeContext(hours: Int): TimeContext {
    return when (hours) {
        in 5..10 -> TimeContext("☀")
        in 11..16 -> TimeContext("▣")
        in 17..20 -> TimeContext("◐")
        else -> TimeContext("☾")
    }
}

@Preview(showBackground = true, widthDp = 1280, heightDp = 800)
@Composable
private fun ClockLearningLandscapePreview() {
    SchoolClockTheme {
        ClockLearningContent(
            state = ClockState(
                year = 2028,
                month = 2,
                day = 28,
                hours = 7,
                minutes = 25,
                seconds = 42,
            ),
            onYearMove = {},
            onMonthMove = {},
            onDayMove = {},
            onHourMove = {},
            onMinuteMove = {},
            onSecondMove = {},
            onResumeClock = {},
            onPauseClock = {},
            onYearSelected = {},
            onMonthSelected = {},
            onDaySelected = {},
            onHourValueSelected = {},
            onMinuteValueSelected = {},
            onSecondValueSelected = {},
            onHourSelected = {},
            onMinuteSelected = {},
            onSecondSelected = {},
        )
    }
}
