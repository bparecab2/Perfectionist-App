package com.example.theperfectionist

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun ChangePasswordScrn(navController: NavController) {
    val context = LocalContext.current
    val passwordManager = remember { PasswordManager(context) }
    val activity = context as MainActivity

    var currentPin by remember { mutableStateOf("") }
    var newPin by remember { mutableStateOf("") }
    var confirmPin by remember { mutableStateOf("") }
    var stage by remember { mutableStateOf(0) }
    var message by remember { mutableStateOf("") }

    LaunchedEffect(currentPin, newPin, confirmPin, stage) {
        when (stage) {
            0 -> {
                if (currentPin.length == 6) {
                    if (currentPin == passwordManager.getPassword()) {
                        stage = 1
                        message = ""
                    } else {
                        message = "Current PIN is wrong"
                        currentPin = ""
                    }
                }
            }
            1 -> {
                if (newPin.length == 6) {
                    stage = 2
                    message = ""
                }
            }
            2 -> {
                if (confirmPin.length == 6) {
                    if (newPin == confirmPin) {
                        passwordManager.savePassword(newPin)
                        message = "PIN changed successfully"
                        currentPin = ""
                        newPin = ""
                        confirmPin = ""
                        stage = 0
                    } else {
                        message = "New PINs do not match"
                        newPin = ""
                        confirmPin = ""
                        stage = 1
                    }
                }
            }
        }
    }

    val isDark = activity.isDarkMode

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
            }

            Text(
                text = "Change Password",
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(modifier = Modifier.height(18.dp))

            ChangePinField(
                label = "Current PIN",
                pinLength = currentPin.length,
                isActive = stage == 0
            )

            Spacer(modifier = Modifier.height(12.dp))

            ChangePinField(
                label = "New 6-digit PIN",
                pinLength = newPin.length,
                isActive = stage == 1
            )

            Spacer(modifier = Modifier.height(12.dp))

            ChangePinField(
                label = "Confirm New PIN",
                pinLength = confirmPin.length,
                isActive = stage == 2
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (message.isNotEmpty()) {
                Text(
                    text = message,
                    color = if (message.contains("success")) {
                        Color(0xFF4CAF50)
                    } else {
                        Color.Red
                    }
                )
            } else {
                Text(
                    text = when (stage) {
                        0 -> "Enter your current PIN"
                        1 -> "Enter your new PIN"
                        else -> "Confirm your new PIN"
                    },
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            ChangePasswordPinPad(
                isDark = isDark,
                onDigitPressed = { digit ->
                    when (stage) {
                        0 -> if (currentPin.length < 6) {
                            currentPin += digit
                            message = ""
                        }
                        1 -> if (newPin.length < 6) {
                            newPin += digit
                            message = ""
                        }
                        2 -> if (confirmPin.length < 6) {
                            confirmPin += digit
                            message = ""
                        }
                    }
                },
                onDeletePressed = {
                    when (stage) {
                        0 -> if (currentPin.isNotEmpty()) {
                            currentPin = currentPin.dropLast(1)
                            message = ""
                        }
                        1 -> if (newPin.isNotEmpty()) {
                            newPin = newPin.dropLast(1)
                            message = ""
                        }
                        2 -> if (confirmPin.isNotEmpty()) {
                            confirmPin = confirmPin.dropLast(1)
                            message = ""
                        }
                    }
                }
            )

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun ChangePinField(
    label: String,
    pinLength: Int,
    isActive: Boolean
) {
    val borderColor = if (isActive) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.onBackground.copy(alpha = 0.35f)
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(6.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.5.dp,
                    color = borderColor,
                    shape = RoundedCornerShape(10.dp)
                )
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(6) { index ->
                val filled = index < pinLength

                Box(
                    modifier = Modifier
                        .size(14.dp)
                        .background(
                            color = if (filled) MaterialTheme.colorScheme.primary else Color.Transparent,
                            shape = CircleShape
                        )
                        .border(
                            width = 1.5.dp,
                            color = borderColor,
                            shape = CircleShape
                        )
                )

                if (index < 5) {
                    Spacer(modifier = Modifier.width(12.dp))
                }
            }
        }
    }
}

@Composable
private fun ChangePasswordPinPad(
    isDark: Boolean,
    onDigitPressed: (String) -> Unit,
    onDeletePressed: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        ChangePasswordPinRow(listOf("1", "2", "3"), isDark, onDigitPressed)
        ChangePasswordPinRow(listOf("4", "5", "6"), isDark, onDigitPressed)
        ChangePasswordPinRow(listOf("7", "8", "9"), isDark, onDigitPressed)

        Row(
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.size(85.dp))

            ChangePasswordPinButton(
                text = "0",
                isDark = isDark,
                onClick = { onDigitPressed("0") }
            )

            Button(
                onClick = onDeletePressed,
                modifier = Modifier.size(85.dp),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent
                ),
                border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 0.dp,
                    pressedElevation = 0.dp
                )
            ) {
                Text(
                    text = "⌫",
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 22.sp
                )
            }
        }
    }
}

@Composable
private fun ChangePasswordPinRow(
    digits: List<String>,
    isDark: Boolean,
    onDigitPressed: (String) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        digits.forEach { digit ->
            ChangePasswordPinButton(
                text = digit,
                isDark = isDark,
                onClick = { onDigitPressed(digit) }
            )
        }
    }
}

@Composable
private fun ChangePasswordPinButton(
    text: String,
    isDark: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier.size(85.dp),
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent
        ),
        border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 0.dp,
            pressedElevation = 0.dp
        )
    ) {
        Text(
            text = text,
            color = MaterialTheme.colorScheme.primary,
            fontSize = 24.sp
        )
    }
}