package com.example.theperfectionist


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi


class MainActivity : ComponentActivity() {

    lateinit var btManager: BluetoothManager


    private val btPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission())
    {
            granted -> Log.d("BT", if (granted) "BLUETOOTH_CONNECT granted" else "BLUETOOTH_CONNECT denied")
    }

    private val enableBluetoothLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult())
    {
            result -> if (result.resultCode == Activity.RESULT_OK)
    {
        Log.d("BT", "Bluetooth enabled")
    }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        btManager = BluetoothManager(this)

        // Request permission
        btPermissionLauncher.launch(android.Manifest.permission.BLUETOOTH_CONNECT)

        // Enable Bluetooth if needed
        val adapter = btManager.getPairedDevices() // triggers permission check

        if (adapter == null) {
            val intent = Intent(android.bluetooth.BluetoothAdapter.ACTION_REQUEST_ENABLE)
            enableBluetoothLauncher.launch(intent)
        }


        setContent {
            val navController = rememberNavController()
            NavHost(navController = navController, startDestination = "screen_1", builder = {
                composable("screen_1") {
                    Screen1(navController)
                }
                composable("screen_2") {
                    Screen2(navController)
                }

                composable("settings") {
                    SettingScreen(navController)
                }

                composable("good") {
                    GoodButton(navController)
                }

                composable("bad") {
                    BadButton(navController)
                }

                composable("Account") {
                    AccountScrn(navController)
                }

                composable("WiFi") {
                    WiFiScrn(navController)
                }

                composable("Bluetooth") {
                    BluetoothScrn(navController)
                }

                composable("Sound") {
                    SoundScrn(navController)
                }


            })
        }
    }

}
