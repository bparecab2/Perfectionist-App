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


class Permissions {

    lateinit var btManager: BluetoothManager

    @RequiresApi(Build.VERSION_CODES.S)
    private val btPermissionsLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { perms ->
            val connectGranted = perms[android.Manifest.permission.BLUETOOTH_CONNECT] == true
            val scanGranted = perms[android.Manifest.permission.BLUETOOTH_SCAN] == true
            Log.d("BT", "CONNECT=$connectGranted, SCAN=$scanGranted")
        }

    private val locationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { perms ->
            val fineGranted = perms[android.Manifest.permission.ACCESS_FINE_LOCATION] == true
            val coarseGranted = perms[android.Manifest.permission.ACCESS_COARSE_LOCATION] == true
            Log.d("LOC", "FINE=$fineGranted, COARSE=$coarseGranted")
        }


    private val enableBluetoothLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                Log.d("BT", "Bluetooth enabled")
            }
        }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        btManager = BluetoothManager(this)

        // Request BOTH permissions
        btPermissionsLauncher.launch(
            arrayOf(
                android.Manifest.permission.BLUETOOTH_CONNECT,
                android.Manifest.permission.BLUETOOTH_SCAN
            )
        )
        // Request both permissions
        locationPermissionLauncher.launch(
            arrayOf(
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )

        // Optional: enable Bluetooth if off
        val systemAdapter = android.bluetooth.BluetoothAdapter.getDefaultAdapter()
        if (systemAdapter != null && !systemAdapter.isEnabled) {
            val intent = Intent(android.bluetooth.BluetoothAdapter.ACTION_REQUEST_ENABLE)
            enableBluetoothLauncher.launch(intent)
        }

    }

}