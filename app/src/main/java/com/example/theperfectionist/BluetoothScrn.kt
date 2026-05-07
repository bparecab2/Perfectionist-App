package com.example.theperfectionist

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@SuppressLint("MissingPermission", "ContextCastToActivity")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BluetoothScrn(navController: NavController) {

    val activity = androidx.compose.ui.platform.LocalContext.current as MainActivity
    val bt = activity.btManager

    if (bt.isConnected && bt.connectedDevice != null) {
        LaunchedEffect(Unit) {
            navController.navigate("screen_3")
        }
        return
    }

    var bleDevices by remember { mutableStateOf(emptyList<BluetoothDevice>()) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Bluetooth Scanner",
                        color = AppThemeState.textColor
                    )
                },

                //  BACK ARROW REMOVED (navigationIcon deleted)

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
                            onClick = { navController.navigate("Account") }
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = AppThemeState.topBarColor
                )
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(AppThemeState.backgroundColor)
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(30.dp))

            Button(
                onClick = {
                    bleDevices = emptyList()

                    bt.startBleScan { device ->
                        val name = device.name ?: return@startBleScan

                        if (!name.equals("Perfectionist", ignoreCase = true)) return@startBleScan

                        if (bleDevices.none { it.address == device.address }) {
                            bleDevices = bleDevices + device
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = AppThemeState.buttonColor
                ),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(2.dp, AppThemeState.buttonBorderColor),
                contentPadding = PaddingValues(16.dp)
            ) {
                Text("Scan for Devices", color = AppThemeState.textColor)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = { navController.navigate("posture_history") },
                colors = ButtonDefaults.buttonColors(
                    containerColor = AppThemeState.buttonColor
                ),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(2.dp, AppThemeState.buttonBorderColor),
                contentPadding = PaddingValues(16.dp)
            ) {
                Text("View Saved Posture Graph", color = AppThemeState.textColor)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Perfectionist Devices",
                color = AppThemeState.textColor
            )

            Spacer(modifier = Modifier.height(10.dp))

            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                items(bleDevices) { device ->
                    DeviceCard(
                        name = device.name ?: "Perfectionist",
                        address = device.address
                    ) {
                        navController.navigate("calibration/${device.address}")
                    }
                }
            }
        }
    }
}

@Composable
fun DeviceCard(name: String, address: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .padding(vertical = 6.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(
            containerColor = AppThemeState.cardColor
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = name, color = AppThemeState.textColor)
            Text(text = address, color = AppThemeState.subTextColor)
        }
    }
}