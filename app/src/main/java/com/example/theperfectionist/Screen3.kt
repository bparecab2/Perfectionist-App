package com.example.theperfectionist

import android.Manifest
import android.R.attr.navigationIcon
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.os.Build

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.bluetooth.BluetoothDevice
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

import androidx.compose.ui.platform.LocalContext

import androidx.compose.material3.TopAppBar
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.TopAppBarDefaults


@Composable
fun Screen3(navController: NavController)
{

    Box(Modifier.fillMaxSize().background(Color(0xFFA2CCFF).copy(alpha = 0.85f)))

    val permissions = buildList {
        add(Manifest.permission.ACCESS_FINE_LOCATION)
        add(Manifest.permission.ACCESS_COARSE_LOCATION)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            add(Manifest.permission.BLUETOOTH_SCAN)
            add(Manifest.permission.BLUETOOTH_CONNECT)
        }
    }.toTypedArray()

    var permissionsGranted by remember { mutableStateOf(false) }

    //Declare Bluetooth enable launcher first
    val enableBluetoothLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == android.app.Activity.RESULT_OK) {
                navController.navigate("Bluetooth")
            }
        }




    // Declare permission launcher after, so it can call the Bluetooth launcher
    val permissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { results ->
            permissionsGranted = results.values.all { it }
            if (permissionsGranted) {
                val enableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                enableBluetoothLauncher.launch(enableIntent)
            }
        }
/*//Row(Modifier.offset(0.dp, 30.dp)) //Back arrow original position for Brian's phone screen size
        Row(modifier = Modifier.fillMaxWidth().padding(top = 30.dp, start = 10.dp, end = 10.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) //This line will automatically adjust the button position based on phone screen size
        {
            IconButton(onClick = { navController.navigate("screen_2") }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Localized description"
                )
            }

            var expanded by remember { mutableStateOf(false) }
//Row(Modifier.offset(300.dp, 30.dp)) //original way
            Box {

                IconButton(onClick = { expanded = !expanded })
                {
                    // Icon(imageVector = Icons.Filled.Menu, contentDescription = "More options") //Hamburger Bar
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "More options"
                    ) // 3 Vertical Dots
                }
                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false })
                {
                    DropdownMenuItem(
                        text = { Text("Account") },
                        onClick = { navController.navigate("Account") })

                    DropdownMenuItem(
                        text = { Text("Wifi") },
                        onClick = { navController.navigate("WiFi") })

                    DropdownMenuItem(
                        text = { Text("Bluetooth") },
                        onClick = { navController.navigate("Bluetooth") })

                    DropdownMenuItem(
                        text = { Text("Sound") },
                        onClick = { navController.navigate("Sound") })
                }

            }
        }*/
    @OptIn(ExperimentalMaterial3Api::class)
    TopAppBar(
        title = {
            Text(
                text = "",
                color = Color(0xFF003366),
                style = androidx.compose.material3.MaterialTheme.typography.titleLarge
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
                DropdownMenuItem(
                    text = { Text("Account") },
                    onClick = { navController.navigate("Account") }
                )
                DropdownMenuItem(   //remove wifi section (most likely will not need it)
                    text = { Text("Wifi") },
                    onClick = { navController.navigate("WiFi") }
                )
                DropdownMenuItem(
                    text = { Text("Bluetooth") },
                    onClick = { navController.navigate("Bluetooth") }
                )
                DropdownMenuItem(
                    text = { Text("Sound") },
                    onClick = { navController.navigate("Sound") }
                )
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Color(0xFFA2CCFF).copy(alpha = 0.85f),
            navigationIconContentColor = Color(0xFF003366),
            actionIconContentColor = Color(0xFF003366),
            titleContentColor = Color(0xFF003366)
        )
    )
    Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center)
    {


        //Text(text = "")

       /* Button(onClick = {
            navController.navigate("")
        }) {
            Text(text = "Run settings")
        }*/

        val context = LocalContext.current
        val activity = context as? MainActivity
        val bt = activity?.btManager
        val isConnected = bt?.isConnected == true
        val connectedDevice = bt?.connectedDevice


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


        Button(onClick = {

            permissionLauncher.launch(permissions)
        }
            ,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF03DAC5).copy(alpha = 0.15f)),
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(2.dp, Color(0xFF009688).copy(alpha = 0.4f)),
            //elevation = ButtonDefaults.buttonElevation(8.dp),
            elevation = null,
            contentPadding = PaddingValues(16.dp)

        )
        {
            Text(text = "Bluetooth", color = Color.DarkGray)
        }

        //remove/comment out the settings button
        /*Button(onClick = {
            navController.navigate("settings")
        }

            ,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF03DAC5).copy(alpha = 0.15f)),
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(2.dp, Color(0xFF009688).copy(alpha = 0.4f)),
            //elevation = ButtonDefaults.buttonElevation(8.dp),
            elevation = null,
            contentPadding = PaddingValues(16.dp)
        )

        {
            Text(text = "Settings", color = Color.DarkGray)
        }*/


    }
}