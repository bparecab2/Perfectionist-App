package com.example.theperfectionist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserManualScrn(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppThemeState.backgroundColor)
    ) {
        CenterAlignedTopAppBar(
            title = {
                Text(
                    text = "User Manual",
                    color = AppThemeState.textColor
                )
            },
            navigationIcon = {
                IconButton(onClick = { navController.navigate("Account") }) {
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
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {
            ManualSection(
                title = "1. Starting the App",
                body = "Open the app and enter your 6-digit PIN. If this is your first time, the app will ask you to create a PIN."
            )

            ManualSection(
                title = "2. Connecting Bluetooth",
                body = "Go to the Bluetooth scanner and press Scan for Devices. Select the device named Perfectionist. The app will connect to the Arduino and check if it is already calibrated."
            )

            ManualSection(
                title = "3. If the Device Is Already Calibrated",
                body = "If the Arduino sends calibrated data, the app will go straight to the Live Posture Data screen."
            )

            ManualSection(
                title = "4. If the Device Is Not Calibrated",
                body = "If the device is not calibrated, the app will take you to Goal Setup. Choose how many days you want your posture goal to last, then press Start My Journey."
            )

            ManualSection(
                title = "5. Calibration Steps",
                body = "The app will guide you through four recordings: Standing Normal, Standing Ideal, Sitting Relaxed, and Sitting Ideal. Press Start Recording on each screen and hold the posture for 8 seconds."
            )

            ManualSection(
                title = "6. Live Posture Data",
                body = "After calibration, the Live Posture Data screen shows roll, pitch, posture family, and posture status. This is where you can see if your posture is good or if the device is warning you."
            )

            ManualSection(
                title = "7. Chart Log",
                body = "The Chart Log shows saved posture data from the phone. When you start a new calibration, the old graph data is cleared so the graph matches the new calibration."
            )

            ManualSection(
                title = "8. Notifications",
                body = "Press the Notification button to open notification settings. You can change sound and vibration levels. Long press the bell icon to turn Sleep Mode on or off."
            )

            ManualSection(
                title = "9. Sleep Mode",
                body = "Sleep Mode silences sound and vibration alerts. When Sleep Mode is on, the bell turns red. Long press the bell again to restore the previous sound and vibration levels."
            )

            ManualSection(
                title = "10. Bad Posture Alerts",
                body = "When the Arduino sends VIBRATING, the phone sends a notification that says Bad posture detected. The notification uses your sound and vibration settings."
            )

            ManualSection(
                title = "11. Account Settings",
                body = "In Account, you can change your PIN, turn Dark Mode on or off, and open this user manual."
            )

            ManualSection(
                title = "12. Dark Mode",
                body = "Dark Mode changes the app background, text, cards, buttons, and calibration screens to darker colors."
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun ManualSection(
    title: String,
    body: String
) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        color = AppThemeState.textColor
    )

    Spacer(modifier = Modifier.height(6.dp))

    Text(
        text = body,
        style = MaterialTheme.typography.bodyMedium,
        color = AppThemeState.subTextColor
    )

    Spacer(modifier = Modifier.height(18.dp))

    Divider(color = AppThemeState.subTextColor.copy(alpha = 0.25f))

    Spacer(modifier = Modifier.height(18.dp))
}