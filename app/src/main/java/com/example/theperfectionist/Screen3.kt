package com.example.theperfectionist

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Screen3(navController: NavController) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        PostureNotificationHelper.createChannel(context)
    }

    LaunchedEffect(Unit) {
        var previousState = ""

        while (true) {
            val currentState = BleLiveData.postureStateText

            if (
                currentState.equals("VIBRATING", ignoreCase = true) &&
                !previousState.equals("VIBRATING", ignoreCase = true)
            ) {
                PostureNotificationHelper.showBadPostureNotification(context)
            }

            previousState = currentState
            delay(100)
        }
    }

    Scaffold(
        topBar = { Screen3TopBar(navController) },
        bottomBar = { BottomNavBar(navController) }
    ) { innerPadding ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Screen3Content(navController)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Screen3TopBar(navController: NavController) {
    CenterAlignedTopAppBar(
        title = { Text("") },
        actions = {
            var expanded by remember { mutableStateOf(false) }

            IconButton(onClick = { expanded = true }) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "More options",
                    tint = AppThemeState.textColor
                )
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Account") },
                    onClick = {
                        expanded = false
                        navController.navigate("Account")
                    }
                )
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = AppThemeState.topBarColor,
            actionIconContentColor = AppThemeState.textColor
        )
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BottomNavBar(navController: NavController) {
    val context = LocalContext.current
    val activity = context as MainActivity
    val bt = activity.btManager

    val mac = bt.connectedDevice?.address ?: ""

    var notificationCount by remember { mutableStateOf(0) }
    var confirmCalibrate by remember { mutableStateOf(false) }

    LaunchedEffect(confirmCalibrate) {
        if (confirmCalibrate) {
            delay(3000)
            confirmCalibrate = false
        }
    }

    NavigationBar(
        containerColor = AppThemeState.topBarColor
    ) {
        NavigationBarItem(
            selected = false,
            onClick = {
                if (mac.isEmpty()) return@NavigationBarItem

                if (!confirmCalibrate) {
                    confirmCalibrate = true
                } else {
                    val storage = PostureStorage(context)
                    storage.clearAllSamples()

                    sendBleCommand("S")
                    navController.navigate("stand_normal/$mac")

                    confirmCalibrate = false
                }
            },
            icon = {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = "Calibrate",
                    tint = AppThemeState.subTextColor
                )
            },
            label = {
                if (confirmCalibrate) {
                    Box(
                        modifier = Modifier
                            .background(Color.Red, RoundedCornerShape(12.dp))
                            .padding(horizontal = 10.dp, vertical = 3.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Confirm",
                            color = Color.Black,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                } else {
                    Text("Calibrate", color = AppThemeState.subTextColor)
                }
            }
        )

        NavigationBarItem(
            selected = false,
            onClick = {
                confirmCalibrate = false
                navController.navigate("posture_history")
            },
            icon = {
                Icon(
                    imageVector = Icons.Filled.List,
                    contentDescription = "Chart Log",
                    tint = AppThemeState.subTextColor
                )
            },
            label = { Text("Chart Log", color = AppThemeState.subTextColor) }
        )

        NavigationBarItem(
            selected = false,
            onClick = {
                confirmCalibrate = false
                navController.navigate("notification_settings")
            },
            icon = {
                BadgedBox(
                    badge = {
                        if (notificationCount > 0) {
                            Badge { Text(notificationCount.toString()) }
                        }
                    }
                ) {
                    Box(
                        modifier = Modifier.combinedClickable(
                            onClick = {
                                confirmCalibrate = false
                                navController.navigate("notification_settings")
                            },
                            onLongClick = {
                                confirmCalibrate = false
                                NotificationSettingsState.toggleSleepMode()
                            }
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Notifications,
                            contentDescription = "Notifications",
                            tint = if (NotificationSettingsState.sleepMode) {
                                Color.Red
                            } else {
                                AppThemeState.subTextColor
                            }
                        )
                    }
                }
            },
            label = { Text("Notification", color = AppThemeState.subTextColor) }
        )
    }
}

@Composable
fun Screen3Content(navController: NavController) {
    Column(
        Modifier
            .fillMaxSize()
            .background(AppThemeState.backgroundColor)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Live Posture Data",
            style = MaterialTheme.typography.headlineMedium,
            color = AppThemeState.textColor
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text("Status: ${BleLiveData.status}", color = AppThemeState.subTextColor)

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            "Roll: ${BleLiveData.roll}°",
            style = MaterialTheme.typography.headlineSmall,
            color = AppThemeState.subTextColor
        )

        Text(
            "Pitch: ${BleLiveData.pitch}°",
            style = MaterialTheme.typography.headlineSmall,
            color = AppThemeState.subTextColor
        )

        Text(
            "Family: ${BleLiveData.familyText}",
            style = MaterialTheme.typography.headlineSmall,
            color = AppThemeState.subTextColor
        )

        Text(
            "Posture: ${BleLiveData.postureStateText}",
            style = MaterialTheme.typography.headlineSmall,
            color = AppThemeState.subTextColor
        )
    }
}