package com.example.theperfectionist

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController



@SuppressLint("ContextCastToActivity")
@Composable
fun BluetoothScrn(navController: NavController) {
    Column(
        Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Bluetooth Settings")
    }

    val activity = LocalContext.current as MainActivity
    val bt = activity.btManager

    val paired = bt.getPairedDevices()
    paired?.forEach {
        Text(text = it.name, modifier = Modifier.clickable {
            bt.connectToDevice(it) { socket ->
                bt.listenForMessages(socket)
                bt.send(socket, "Hello!")
            }
        })
    }


}


