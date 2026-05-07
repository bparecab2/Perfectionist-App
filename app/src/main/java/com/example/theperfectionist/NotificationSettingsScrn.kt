package com.example.theperfectionist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

object NotificationSettingsState {
    var soundLevel by mutableFloatStateOf(1f)
    var vibrationLevel by mutableFloatStateOf(1f)

    var previousSoundLevel by mutableFloatStateOf(1f)
    var previousVibrationLevel by mutableFloatStateOf(1f)

    var sleepMode by mutableStateOf(false)

    fun toggleSleepMode() {
        if (!sleepMode) {
            previousSoundLevel = soundLevel
            previousVibrationLevel = vibrationLevel
            soundLevel = 0f
            vibrationLevel = 0f
            sleepMode = true
        } else {
            soundLevel = previousSoundLevel
            vibrationLevel = previousVibrationLevel
            sleepMode = false
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationSettingsScrn(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppThemeState.backgroundColor)
    ) {
        CenterAlignedTopAppBar(
            title = {
                Text(
                    text = "Notification Settings",
                    color = AppThemeState.textColor
                )
            },
            navigationIcon = {
                IconButton(onClick = { navController.navigate("screen_3") }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = AppThemeState.textColor
                    )
                }
            },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = AppThemeState.topBarColor
            )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 28.dp, vertical = 24.dp)
        ) {
            Text(
                text = "Sleep Mode: ${if (NotificationSettingsState.sleepMode) "ON" else "OFF"}",
                style = MaterialTheme.typography.titleMedium,
                color = AppThemeState.textColor
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Sound: ${(NotificationSettingsState.soundLevel * 100).toInt()}%",
                style = MaterialTheme.typography.bodyLarge,
                color = AppThemeState.textColor
            )

            Spacer(modifier = Modifier.height(18.dp))

            Slider(
                value = NotificationSettingsState.soundLevel,
                onValueChange = {
                    NotificationSettingsState.soundLevel = it
                    if (NotificationSettingsState.sleepMode && it > 0f) {
                        NotificationSettingsState.sleepMode = false
                    }
                },
                colors = SliderDefaults.colors(
                    thumbColor = AppThemeState.purpleColor,
                    activeTrackColor = AppThemeState.purpleColor,
                    inactiveTrackColor = AppThemeState.purpleColor.copy(alpha = 0.35f)
                )
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Vibration: ${(NotificationSettingsState.vibrationLevel * 100).toInt()}%",
                style = MaterialTheme.typography.bodyLarge,
                color = AppThemeState.textColor
            )

            Spacer(modifier = Modifier.height(18.dp))

            Slider(
                value = NotificationSettingsState.vibrationLevel,
                onValueChange = {
                    NotificationSettingsState.vibrationLevel = it
                    if (NotificationSettingsState.sleepMode && it > 0f) {
                        NotificationSettingsState.sleepMode = false
                    }
                },
                colors = SliderDefaults.colors(
                    thumbColor = AppThemeState.purpleColor,
                    activeTrackColor = AppThemeState.purpleColor,
                    inactiveTrackColor = AppThemeState.purpleColor.copy(alpha = 0.35f)
                )
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Long press the bell to silence all alerts.",
                style = MaterialTheme.typography.bodyLarge,
                color = AppThemeState.textColor
            )
        }
    }
}