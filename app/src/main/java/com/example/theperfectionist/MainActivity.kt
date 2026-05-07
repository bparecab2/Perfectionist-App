@file:Suppress("DEPRECATION")

package com.example.theperfectionist

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

class MainActivity : ComponentActivity() {

    lateinit var btManager: BluetoothManager


    private val notificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            Log.d("NOTIF", "POST_NOTIFICATIONS granted = $granted")
        }
   
    private val locationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { perms ->
            val fine = perms[Manifest.permission.ACCESS_FINE_LOCATION] == true
            val coarse = perms[Manifest.permission.ACCESS_COARSE_LOCATION] == true
            Log.d("LOC", "FINE=$fine, COARSE=$coarse")

            if (fine || coarse) {
                requestEnableBluetooth()
            }
        }


    @RequiresApi(Build.VERSION_CODES.S)
    private val btPermissionsLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { perms ->
            val connectGranted = perms[Manifest.permission.BLUETOOTH_CONNECT] == true
            val scanGranted = perms[Manifest.permission.BLUETOOTH_SCAN] == true

            Log.d("BT", "CONNECT=$connectGranted, SCAN=$scanGranted")

            if (connectGranted && scanGranted) {
                requestEnableBluetooth()
            }
        }


    private val enableBluetoothLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                Log.d("BT", "Bluetooth enabled")
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        btManager = BluetoothManager(this)


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }


        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {

                btPermissionsLauncher.launch(
                    arrayOf(
                        Manifest.permission.BLUETOOTH_CONNECT,
                        Manifest.permission.BLUETOOTH_SCAN
                    )
                )
            }

            else -> {

                locationPermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
        }


        setContent {
            val navController = rememberNavController()
            val context = LocalContext.current

            AppThemeState.loadTheme(context)

            val passwordManager = remember { PasswordManager(context) }

            val startDestination = if (passwordManager.hasPassword()) {
                "password"
            } else {
                "set_password"
            }

            NavHost(
                navController = navController,
                startDestination = startDestination
            ) {
                composable("set_password") { SetPasswordScrn(navController) }
                composable("password") { PasswordScrn(navController) }
                composable("change_password") { ChangePasswordScrn(navController) }

                composable("screen_3") { Screen3(navController) }

                composable("Account") { AccountScrn(navController) }
                composable("Bluetooth") { BluetoothScrn(navController) }
                composable("notification_settings") { NotificationSettingsScrn(navController) }
                composable("posture_history") { PostureHistoryScrn(navController) }

                composable("user_manual") { UserManualScrn(navController) }

                composable("stand_normal/{mac}") {
                    StandNormal(navController, it.arguments?.getString("mac") ?: "")
                }

                composable("stand_ideal/{mac}") {
                    StandIdeal(navController, it.arguments?.getString("mac") ?: "")
                }

                composable("sit_relaxed/{mac}") {
                    SitNormal(navController, it.arguments?.getString("mac") ?: "")
                }

                composable("sit_ideal/{mac}") {
                    SitIdeal(navController, it.arguments?.getString("mac") ?: "")
                }

                composable("target_date/{mac}") {
                    TargetDateScrn(navController, it.arguments?.getString("mac") ?: "")
                }

                composable("calibration/{mac}") { backStack ->
                    val mac = backStack.arguments?.getString("mac") ?: ""
                    val adapter = BluetoothAdapter.getDefaultAdapter()
                    val device = adapter.getRemoteDevice(mac)

                    CalibrationScrn(device = device, navController = navController)
                }
            }
        }
    }

    private fun requestEnableBluetooth() {
        val adapter = BluetoothAdapter.getDefaultAdapter()
        if (adapter != null && !adapter.isEnabled) {
            val intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            enableBluetoothLauncher.launch(intent)
        }
    }
}
