package com.example.theperfectionist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            NavHost(navController = navController, startDestination = "screen_1", builder = {
                composable("screen_1"){
                    Screen1(navController)
                }
                composable("screen_2"){
                    Screen2(navController)
                }

                composable("settings"){
                    SettingScreen(navController)
                }

                composable("good"){
                    GoodButton(navController)
                }

                composable("bad"){
                    BadButton(navController)
                }

                composable("Account"){
                    AccountScrn(navController)
                }

                composable("WiFi"){
                    WiFiScrn(navController)
                }

                composable("Bluetooth"){
                    BluetoothScrn(navController)
                }

                composable("Sound"){
                    SoundScrn(navController)
                }



            })
        }
    }
}
