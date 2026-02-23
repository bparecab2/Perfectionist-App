package com.example.theperfectionist

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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

    var connectionStatus by remember { mutableStateOf("Not connected") }

    // NEW: Messaging state
    var messageInput by remember { mutableStateOf("") }
    var messageLog by remember { mutableStateOf(listOf<String>()) }
    var activeSocket by remember { mutableStateOf<android.bluetooth.BluetoothSocket?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(text = "Bluetooth Scanner")
        Spacer(modifier = Modifier.height(10.dp))

        Text(text = "Status: $connectionStatus")
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

        // PAIRED DEVICES
        Text("Paired Devices")
        pairedDevices.forEach { device ->
            DeviceCard(device.name ?: "Unknown", device.address) {

                connectionStatus = "Connecting to ${device.name}..."

                bt.connectToDevice(device) { socket ->
                    activeSocket = socket
                    connectionStatus = "Connected to ${device.name}"

                    // Listen for messages
                    bt.listenForMessages(socket)

                    // Add incoming messages to log
                    Thread {
                        val input = socket.inputStream
                        val buffer = ByteArray(1024)
                        while (true) {
                            val bytes = input.read(buffer)
                            val msg = String(buffer, 0, bytes)
                            messageLog = messageLog + "Device: $msg"
                        }
                    }.start()
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // AVAILABLE DEVICES
        Text("Available Devices")
        availableDevices.forEach { device ->
            DeviceCard(device.name ?: "Unknown", device.address) {

                connectionStatus = "Connecting to ${device.name}..."

                bt.connectToDevice(device) { socket ->
                    activeSocket = socket
                    connectionStatus = "Connected to ${device.name}"

                    Thread {
                        val input = socket.inputStream
                        val buffer = ByteArray(1024)
                        while (true) {
                            val bytes = input.read(buffer)
                            val msg = String(buffer, 0, bytes)
                            messageLog = messageLog + "Device: $msg"
                        }
                    }.start()
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // BLE DEVICES
        Text("BLE Devices")
        bleDevices.forEach { device ->
            DeviceCard(device.name ?: "Unknown BLE Device", device.address) {

                connectionStatus = "Connecting to ${device.name ?: "BLE Device"}..."

                bt.connectToDevice(device) { socket ->
                    activeSocket = socket
                    connectionStatus = "Connected to ${device.name ?: "BLE Device"}"

                    Thread {
                        val input = socket.inputStream
                        val buffer = ByteArray(1024)
                        while (true) {
                            val bytes = input.read(buffer)
                            val msg = String(buffer, 0, bytes)
                            messageLog = messageLog + "Device: $msg"
                        }
                    }.start()
                }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        // MESSAGING UI
        if (activeSocket != null) {
            Text("Messaging")
            Spacer(modifier = Modifier.height(10.dp))

            // Message log
            messageLog.forEach { msg ->
                Text(text = msg)
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Input + Send
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = messageInput,
                    onValueChange = { messageInput = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Type a message") }
                )

                Spacer(modifier = Modifier.width(10.dp))

                Button(onClick = {
                    val socket = activeSocket
                    if (socket != null) {
                        bt.send(socket, messageInput)
                        messageLog = messageLog + "You: $messageInput"
                        messageInput = ""
                    }
                }) {
                    Text("Send")
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
