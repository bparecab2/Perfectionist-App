package com.example.theperfectionist

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@SuppressLint("MissingPermission", "ContextCastToActivity")
@Composable
fun BluetoothScrn(navController: NavController) {

    val activity = LocalContext.current as MainActivity
    val bt = activity.btManager

    var pairedDevices by remember { mutableStateOf(bt.getPairedDevices()?.toList() ?: emptyList<BluetoothDevice>()) }
    var availableDevices by remember { mutableStateOf(emptyList<BluetoothDevice>()) }
    var bleDevices by remember { mutableStateOf(emptyList<BluetoothDevice>()) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // HEADER + SCAN BUTTON
        item {
            Text("Bluetooth Scanner")
            Spacer(modifier = Modifier.height(20.dp))

            Button(onClick = {
                availableDevices = emptyList()
                bleDevices = emptyList()

                bt.startDiscovery { device ->
                    availableDevices = availableDevices + device
                }

                bt.startBleScan { device ->
                    bleDevices = bleDevices + device
                }
            }) {
                Text("Scan for Devices")
            }

            Spacer(modifier = Modifier.height(20.dp))
            Text("Paired Devices")
        }

        // CLASSIC PAIRED DEVICES
        items(pairedDevices) { device ->
            DeviceCard(device.name ?: "Unknown", device.address) {
                bt.connectToDevice(device) { socket ->
                    if (socket != null) {
                        bt.listenForMessages(socket)
                    } else {
                        // BLE device connected (GATT)
                        // No socket exists for BLE
                    }
                }
            }
        }

        // AVAILABLE DEVICES
        item {
            Spacer(modifier = Modifier.height(20.dp))
            Text("Available Devices")
        }

        items(availableDevices) { device ->
            DeviceCard(device.name ?: "Unknown", device.address) {
                bt.connectToDevice(device) { socket ->
                    if (socket != null) {
                        bt.listenForMessages(socket)
                    } else {
                        // BLE device connected (GATT)
                    }
                }
            }
        }

        // BLE DEVICES
        item {
            Spacer(modifier = Modifier.height(20.dp))
            Text("BLE Devices")
        }

        items(bleDevices) { device ->
            DeviceCard(device.name ?: "Unknown BLE Device", device.address) {
                bt.connectToDevice(device) { socket ->
                    if (socket != null) {
                        bt.listenForMessages(socket)
                    } else {
                        // BLE device connected (GATT)
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
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(text = name)
            Text(text = address)
        }
    }
}
