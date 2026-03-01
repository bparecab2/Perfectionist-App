package com.example.theperfectionist

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CalibrationScreen(
    status: String = "Idle",
    roll: String = "--",
    pitch: String = "--",
    posture: String = "--",
    onDisconnect: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Calibration", style = MaterialTheme.typography.headlineSmall)

        OutlinedButton(onClick = onDisconnect) {
            Text("Disconnect")
        }

        Text("Status: $status", style = MaterialTheme.typography.bodyLarge)

        Spacer(Modifier.height(8.dp))

        Text("Roll: $roll°", style = MaterialTheme.typography.headlineMedium)
        Text("Pitch: $pitch°", style = MaterialTheme.typography.headlineMedium)
        Text("Posture: $posture", style = MaterialTheme.typography.headlineMedium)
    }
}
