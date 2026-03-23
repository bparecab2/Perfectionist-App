package com.example.theperfectionist

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

// ------------------------------------------------------------
// MAIN SCREEN3 COMPOSABLE
// ------------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Screen3(navController: NavController) {

    Scaffold(
        topBar = { Screen3TopBar(navController) },
        bottomBar = { BottomNavBar(navController) },
    ) { innerPadding ->

        Box(
            Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFA2CCFF).copy(alpha = 0.85f))
        ) {
            Screen3Content(navController)
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Screen3TopBar(navController: NavController) {

    CenterAlignedTopAppBar(
        title = {
            Text(
                text = "Settings",
                color = Color(0xFF003366),
                style = MaterialTheme.typography.titleLarge
            )
        },
        navigationIcon = {
            IconButton(onClick = { navController.navigate("screen_2") }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color(0xFF003366)
                )
            }
        },
        actions = {
            var expanded by remember { mutableStateOf(false) }

            IconButton(onClick = { expanded = true }) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "More options",
                    tint = Color(0xFF003366)
                )
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                DropdownMenuItem(text = { Text("Account") }, onClick = { navController.navigate("Account") })
                DropdownMenuItem(text = { Text("Wifi") }, onClick = { navController.navigate("WiFi") })
                DropdownMenuItem(text = { Text("Bluetooth") }, onClick = { navController.navigate("Bluetooth") })
                DropdownMenuItem(text = { Text("Sound") }, onClick = { navController.navigate("Sound") })
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Color(0xFFA2CCFF).copy(alpha = 0.85f),
            navigationIconContentColor = Color(0xFF003366),
            actionIconContentColor = Color(0xFF003366),
            titleContentColor = Color(0xFF003366)
        )
    )
}

@Composable
fun BottomNavBar(navController: NavController) {

    // Read context OUTSIDE onClick (fixes composable error)
    val context = LocalContext.current
    val activity = context as? MainActivity
    val bt = activity?.btManager
    val isConnected = bt?.isConnected == true
    val connectedDevice = bt?.connectedDevice

    NavigationBar(
        containerColor = Color(0xFFA2CCFF).copy(alpha = 0.85f)
    ) {
        val currentRoute = navController.currentBackStackEntry?.destination?.route

        // CALIBRATE — WITH BLUETOOTH LOGIC
        NavigationBarItem(
            selected = currentRoute?.startsWith("calibration") == true,
            onClick = {
                if (isConnected && connectedDevice != null) {
                    navController.navigate("calibration/${connectedDevice.address}")
                } else {
                    navController.navigate("Bluetooth")
                }
            },
            icon = {},
            label = { Text("Calibrate", color = Color.DarkGray) }
        )

        // TEMPLATE 1
        NavigationBarItem(
            selected = currentRoute == "template1",
            onClick = { navController.navigate("template1") },
            icon = {},
            label = { Text("Template 1", color = Color.DarkGray) }
        )

        // TEMPLATE 2
        NavigationBarItem(
            selected = currentRoute == "template2",
            onClick = { navController.navigate("template2") },
            icon = {},
            label = { Text("Template 2", color = Color.DarkGray) }
        )
    }
}


@Composable
fun Screen3Content(navController: NavController) {

/*    val context = LocalContext.current
    val activity = context as? MainActivity
    val bt = activity?.btManager
    val isConnected = bt?.isConnected == true
    val connectedDevice = bt?.connectedDevice*/

    val permissions = buildList {
        add(Manifest.permission.ACCESS_FINE_LOCATION)
        add(Manifest.permission.ACCESS_COARSE_LOCATION)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            add(Manifest.permission.BLUETOOTH_SCAN)
            add(Manifest.permission.BLUETOOTH_CONNECT)
        }
    }.toTypedArray()

    var permissionsGranted by remember { mutableStateOf(false) }

    val enableBluetoothLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == android.app.Activity.RESULT_OK) {
                navController.navigate("Bluetooth")
            }
        }

    val permissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { results ->
            permissionsGranted = results.values.all { it }
            if (permissionsGranted) {
                val enableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                enableBluetoothLauncher.launch(enableIntent)
            }
        }

    Column(
        Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {


        /*
        Button(
            onClick = {
                if (isConnected && connectedDevice != null) {
                    navController.navigate("calibration/${connectedDevice.address}")
                } else {
                    navController.navigate("Bluetooth")
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF03DAC5).copy(alpha = 0.15f)),
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(2.dp, Color(0xFF009688).copy(alpha = 0.4f)),
            elevation = null,
            contentPadding = PaddingValues(16.dp)
        ) {
            Text(text = "Calibrate", color = Color.DarkGray)
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = { permissionLauncher.launch(permissions) },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF03DAC5).copy(alpha = 0.15f)),
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(2.dp, Color(0xFF009688).copy(alpha = 0.4f)),
            elevation = null,
            contentPadding = PaddingValues(16.dp)
        ) {
            Text(text = "Bluetooth", color = Color.DarkGray)
        }
        */

        Text(
            text = "Select an option from the navigation bar below",
            color = Color.DarkGray
        )
    }
}
