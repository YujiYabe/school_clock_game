package com.example.schoolclockgame.presentation.clock

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

private const val RepeatInitialDelayMillis = 450L
private const val RepeatIntervalMillis = 90L

@Composable
internal fun RepeatingTextButton(
    text: String,
    onPress: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    fontSize: TextUnit = 24.sp,
    enabledColor: Color = Color(0xFF246BFD),
    disabledColor: Color = Color(0xFFB7BECA),
) {
    val currentOnPress by rememberUpdatedState(onPress)

    Box(
        modifier = modifier
            .defaultMinSize(minWidth = 48.dp, minHeight = 48.dp)
            .semantics {
                role = Role.Button
                if (!enabled) {
                    disabled()
                }
                onClick {
                    if (enabled) {
                        currentOnPress()
                    }
                    enabled
                }
            }
            .pointerInput(enabled) {
                if (!enabled) {
                    return@pointerInput
                }
                detectTapGestures(
                    onPress = {
                        currentOnPress()
                        coroutineScope {
                            val repeatJob = launch {
                                delay(RepeatInitialDelayMillis)
                                while (isActive) {
                                    currentOnPress()
                                    delay(RepeatIntervalMillis)
                                }
                            }
                            try {
                                tryAwaitRelease()
                            } finally {
                                repeatJob.cancel()
                            }
                        }
                    },
                )
            },
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            fontSize = fontSize,
            fontWeight = FontWeight.Black,
            color = if (enabled) enabledColor else disabledColor,
        )
    }
}
