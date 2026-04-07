@file:Suppress("DEPRECATION")

package com.example.theperfectionist


import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue


class MainActivity : ComponentActivity() {

    lateinit var btManager: BluetoothManager
    lateinit var notificationHelper: NotificationHelper


    var isDarkMode by mutableStateOf(false)

    var soundLevel by mutableFloatStateOf(1f)
    var vibrationLevel by mutableFloatStateOf(1f)
    var sleepMode by mutableStateOf(false)

    var prevSoundLevel by mutableFloatStateOf(1f)

    var prevVibrationLevel by mutableFloatStateOf(1f)


    private val notificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        btManager = BluetoothManager(this)

        notificationHelper = NotificationHelper(this)
        notificationHelper.createNotificationChannel()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

        setContent {
            val navController = rememberNavController()
            PerfectionistTheme(darkTheme = isDarkMode) {
                NavHost(navController = navController, startDestination = "screen_1") {
                    composable("screen_1") { Screen1(navController) }
                    composable("screen_2") { Screen2(navController) }
                    composable("screen_3") { Screen3(navController) }
                    composable("settings") { SettingScreen(navController) }
                    //composable("good") { GoodButton(navController) }
                    //composable("bad") { BadButton(navController) }

                    composable("password") { PasswordScrn(navController) }
                    composable("set_password") { SetPasswordScrn(navController) }
                    composable("change_password") { ChangePasswordScrn(navController) }
                    composable("user_manual") { UserManualScrn(navController) }
                    composable("notification_settings") { NotificationSettingsScrn(navController) }

                    composable("Account") { AccountScrn(navController) }
                    composable("WiFi") { WiFiScrn(navController) }
                    composable("Bluetooth") { BluetoothScrn(navController) }
                    composable("posture_history") { PostureHistoryScrn(navController) }
                    composable("Sound") { SoundScrn(navController) }

                    composable("stand_normal/{mac}") { backStack ->
                        val mac = backStack.arguments?.getString("mac")!!
                        StandNormal(navController = navController, mac = mac)
                    }

                    composable("stand_ideal/{mac}") { backStack ->
                        val mac = backStack.arguments?.getString("mac")!!
                        StandIdeal(navController = navController, mac = mac)
                    }

                    composable("sit_relaxed/{mac}") { backStack ->
                        val mac = backStack.arguments?.getString("mac")!!
                        SitNormal(navController = navController, mac = mac)
                    }

                    composable("sit_ideal/{mac}") { backStack ->
                        val mac = backStack.arguments?.getString("mac")!!
                        SitIdeal(navController = navController, mac = mac)
                    }




                    composable("calibration/{mac}") { backStack ->
                        val mac = backStack.arguments?.getString("mac")!!
                        val adapter = BluetoothAdapter.getDefaultAdapter()
                        val device = adapter.getRemoteDevice(mac)

                        CalibrationScrn(
                            device = device,
                            navController = navController,
                            showHome = true
                        )
                    }


                }
            }
        }
    }
}
