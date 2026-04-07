package com.example.theperfectionist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationSettingsScrn(navController: NavController) {
    val activity = LocalContext.current as MainActivity

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        CenterAlignedTopAppBar(
            title = {
                Text(
                    text = "Notification Settings",
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.titleLarge
                )
            },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
            },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = MaterialTheme.colorScheme.background,
                navigationIconContentColor = MaterialTheme.colorScheme.onBackground,
                titleContentColor = MaterialTheme.colorScheme.onBackground
            )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(28.dp)
        ) {
            /*Text(
                text = "Sleep Mode: ${if (activity.sleepMode) "ON" else "OFF"}",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground
            )*/ //Julian's

            Text(
                text = "Sound: ${(activity.soundLevel * 100).toInt()}%",
                color = MaterialTheme.colorScheme.onBackground
            )

            Slider(
                value = activity.soundLevel,
                onValueChange = {
                    if (!activity.sleepMode) {
                        activity.soundLevel = it
                    }
                },
                valueRange = 0f..1f,
                enabled = !activity.sleepMode,
                colors = SliderDefaults.colors(
                    thumbColor = MaterialTheme.colorScheme.primary,
                    activeTrackColor = MaterialTheme.colorScheme.primary,
                    inactiveTrackColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f),
                    disabledThumbColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
                    disabledActiveTrackColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.25f),
                    disabledInactiveTrackColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.15f)
                )
            )

            Text(
                text = "Vibration: ${(activity.vibrationLevel * 100).toInt()}%",
                color = MaterialTheme.colorScheme.onBackground
            )

            Slider(
                value = activity.vibrationLevel,
                onValueChange = {
                    if (!activity.sleepMode) {
                        activity.vibrationLevel = it
                    }
                },
                valueRange = 0f..1f,
                enabled = !activity.sleepMode,
                colors = SliderDefaults.colors(
                    thumbColor = MaterialTheme.colorScheme.primary,
                    activeTrackColor = MaterialTheme.colorScheme.primary,
                    inactiveTrackColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f),
                    disabledThumbColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
                    disabledActiveTrackColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.25f),
                    disabledInactiveTrackColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.15f)
                )
            )

            /*Text(
                text = if (activity.sleepMode) {
                    "Sleep Mode is active. Long press the bell again to restore your previous sound and vibration levels."
                } else {
                    "Long press the bell to silence all alerts."
                },
                color = MaterialTheme.colorScheme.onBackground
            )*/ //Julian's
        }
    }
}