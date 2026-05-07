package com.example.theperfectionist

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.delay

@Composable
fun StandIdeal(navController: NavController, mac: String) {
    var isRecording by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppThemeState.backgroundColor)
            .padding(20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Standing Ideal",
            style = MaterialTheme.typography.headlineMedium,
            color = AppThemeState.textColor
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text =
                "1) Stand with your back against a wall.\n" +
                        "2) Touch back of head, shoulder blades and buttocks to the wall.\n" +
                        "3) Keep heels 2–4 inches away from the wall.\n" +
                        "4) Place one hand behind your lower back.\n" +
                        "5) There should be a small natural gap.\n" +
                        "6) Gently tuck your chin.\n" +
                        "7) Step away from the wall and maintain this alignment.",
            color = AppThemeState.textColor
        )

        Spacer(modifier = Modifier.height(40.dp))

        Button(
            onClick = {
                if (!isRecording) {
                    isRecording = true
                    message = "Connected. Sending B. Recording standing ideal for 8 seconds..."
                }
            },
            enabled = true,
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isRecording) Color(0xFF777777) else AppThemeState.buttonColor,
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(2.dp, AppThemeState.buttonBorderColor)
        ) {
            Text(
                text = "Start Recording",
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = message,
            color = AppThemeState.subTextColor
        )

        if (isRecording) {
            LaunchedEffect(Unit) {
                val ok = sendBleCommand("B")

                if (!ok) {
                    message = "Failed to send B. Recording standing ideal for 8 seconds"
                }

                delay(8000)
                message = "Recording Complete"
                delay(100)

                navController.navigate("sit_relaxed/$mac")
                isRecording = false
            }
        }
    }
}