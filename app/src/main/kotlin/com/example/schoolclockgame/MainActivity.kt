package com.example.schoolclockgame

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.schoolclockgame.presentation.clock.ClockLearningScreen
import com.example.schoolclockgame.ui.theme.SchoolClockTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SchoolClockTheme {
                ClockLearningScreen()
            }
        }
    }
}
