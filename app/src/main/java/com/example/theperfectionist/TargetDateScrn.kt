package com.example.theperfectionist

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TargetDateScrn(navController: NavController, mac: String) {
    val context = androidx.compose.ui.platform.LocalContext.current

    var targetDays by remember { mutableStateOf("14") }
    var errorText by remember { mutableStateOf("") }

    val missionCardColor = if (AppThemeState.darkMode) {
        Color(0xFF3A3A3A)
    } else {
        Color(0xFFEAF4FF)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppThemeState.backgroundColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Goal Setup",
                        color = AppThemeState.textColor,
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = AppThemeState.topBarColor,
                    titleContentColor = AppThemeState.textColor
                )
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = AppThemeState.cardColor
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(22.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Text(
                            text = "You’re all set ✨",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = AppThemeState.textColor
                        )

                        Text(
                            text = "Great start. Better posture is built day by day. Pick a goal window and let’s make this your next healthy habit.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = AppThemeState.subTextColor
                        )

                        Text(
                            text = "Quick goal picks",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = AppThemeState.textColor
                        )

                        Column(
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                GoalChip(
                                    label = "7 days",
                                    selected = targetDays == "7",
                                    onClick = {
                                        targetDays = "7"
                                        errorText = ""
                                    }
                                )
                                GoalChip(
                                    label = "14 days",
                                    selected = targetDays == "14",
                                    onClick = {
                                        targetDays = "14"
                                        errorText = ""
                                    }
                                )
                                GoalChip(
                                    label = "21 days",
                                    selected = targetDays == "21",
                                    onClick = {
                                        targetDays = "21"
                                        errorText = ""
                                    }
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                GoalChip(
                                    label = "30 days",
                                    selected = targetDays == "30",
                                    onClick = {
                                        targetDays = "30"
                                        errorText = ""
                                    }
                                )
                                GoalChip(
                                    label = "45 days",
                                    selected = targetDays == "45",
                                    onClick = {
                                        targetDays = "45"
                                        errorText = ""
                                    }
                                )
                                GoalChip(
                                    label = "60 days",
                                    selected = targetDays == "60",
                                    onClick = {
                                        targetDays = "60"
                                        errorText = ""
                                    }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Or choose your own number",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = AppThemeState.textColor
                        )

                        OutlinedTextField(
                            value = targetDays,
                            onValueChange = {
                                targetDays = it.filter { ch -> ch.isDigit() }
                                errorText = ""
                            },
                            label = {
                                Text(
                                    text = "Target days",
                                    color = AppThemeState.subTextColor
                                )
                            },
                            placeholder = {
                                Text(
                                    text = "Example: 14",
                                    color = AppThemeState.subTextColor
                                )
                            },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number
                            ),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = AppThemeState.textColor,
                                unfocusedTextColor = AppThemeState.textColor,
                                focusedBorderColor = AppThemeState.purpleColor,
                                unfocusedBorderColor = AppThemeState.subTextColor,
                                focusedLabelColor = AppThemeState.purpleColor,
                                unfocusedLabelColor = AppThemeState.subTextColor,
                                cursorColor = AppThemeState.purpleColor,
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent
                            )
                        )

                        val previewDays = targetDays.toIntOrNull()

                        if (previewDays != null && previewDays > 0) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = missionCardColor
                                ),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(14.dp)
                                ) {
                                    Text(
                                        text = "Your posture mission",
                                        fontWeight = FontWeight.SemiBold,
                                        color = AppThemeState.textColor
                                    )

                                    Spacer(modifier = Modifier.height(4.dp))

                                    Text(
                                        text = "We’ll use your posture data to estimate progress over the next $previewDays days.",
                                        color = AppThemeState.subTextColor
                                    )
                                }
                            }
                        }

                        if (errorText.isNotEmpty()) {
                            Text(
                                text = errorText,
                                color = Color.Red,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Button(
                            onClick = {
                                val days = targetDays.toIntOrNull()

                                if (days == null || days <= 0) {
                                    errorText = "Please enter a valid number of days."
                                } else {
                                    TargetPrefs.saveTargetDays(context, days)

                                    navController.navigate("stand_normal/$mac") {
                                        popUpTo("target_date/$mac") { inclusive = true }
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            shape = RoundedCornerShape(18.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (AppThemeState.darkMode) {
                                    Color(0xFF666666)
                                } else {
                                    Color(0xFF005BBB)
                                },
                                contentColor = Color.White
                            )
                        ) {
                            Text(
                                text = "Start My Journey",
                                color = Color.White,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Text(
                            text = "You can update this target later if your plan changes.",
                            style = MaterialTheme.typography.bodySmall,
                            color = AppThemeState.subTextColor
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun GoalChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val chipBorder = if (selected) {
        AppThemeState.purpleColor
    } else {
        AppThemeState.subTextColor.copy(alpha = 0.7f)
    }

    val chipBackground = if (selected) {
        AppThemeState.purpleColor.copy(alpha = 0.22f)
    } else {
        if (AppThemeState.darkMode) Color(0xFF2F2F2F) else Color.White
    }

    Box(
        modifier = Modifier
            .border(
                width = 1.5.dp,
                color = chipBorder,
                shape = RoundedCornerShape(18.dp)
            )
            .background(
                color = chipBackground,
                shape = RoundedCornerShape(18.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Text(
            text = label,
            color = if (selected) AppThemeState.textColor else AppThemeState.subTextColor,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
        )
    }
}