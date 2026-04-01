package com.example.theperfectionist

import android.annotation.SuppressLint
import android.bluetooth.*
import android.content.Context
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothProfile
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

@@ -20,39 +38,37 @@ import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import java.nio.charset.Charset
import java.util.UUID
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlin.math.sqrt

@Composable
fun CalibrationScrn(
    device: BluetoothDevice,          // You pass this from BluetoothScrn
    onDisconnect: () -> Unit = {},     // Optional callback
    device: BluetoothDevice,
    onDisconnect: () -> Unit = {},
    navController: NavController,
    showHome: Boolean
) {
    Box(Modifier.fillMaxSize().background(Color(0xFFA2CCFF).copy(alpha = 0.85f)))
    val context = LocalContext.current
    val storage = remember { PostureStorage(context) }

    // BLE UUIDs
    val SERVICE_UUID = UUID.fromString("12345678-1234-1234-1234-1234567890ab")
    val CHAR_UUID = UUID.fromString("abcdefab-1234-1234-1234-abcdefabcdef")
    val CCCD_UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")
    val serviceUuid = UUID.fromString("12345678-1234-1234-1234-1234567890ab")
    val charUuid = UUID.fromString("abcdefab-1234-1234-1234-abcdefabcdef")
    val cccdUuid = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")

    // BLE connection state
    var gatt by remember { mutableStateOf<BluetoothGatt?>(null) }

    // UI state
    var status by remember { mutableStateOf("Connecting...") }
    var roll by remember { mutableStateOf("--") }
    var pitch by remember { mutableStateOf("--") }
    var posture by remember { mutableStateOf("--") }
    var scoreText by remember { mutableStateOf("--") }
    var savedCount by remember { mutableStateOf(storage.sampleCount()) }
    var lastSavedTime by remember { mutableStateOf(storage.latestSavedTimeText()) }
    var lastSavedMillis by remember { mutableLongStateOf(0L) }

    val activity = context as MainActivity
    val bt = activity.btManager


    // === GATT CALLBACK ===
    val gattCallback = remember {
        object : BluetoothGattCallback() {


            @@ -70,8 +86,8 @@ fun CalibrationScrn(

                @SuppressLint("MissingPermission")
                override fun onServicesDiscovered(g: BluetoothGatt, statusCode: Int) {
                val service = g.getService(SERVICE_UUID)
                val characteristic = service?.getCharacteristic(CHAR_UUID)
                val service = g.getService(serviceUuid)
                val characteristic = service?.getCharacteristic(charUuid)

                if (characteristic == null) {
                    status = "Characteristic not found"

                    @@ -80,10 +96,14 @@ fun CalibrationScrn(

                    g.setCharacteristicNotification(characteristic, true)

                    val cccd = characteristic.getDescriptor(CCCD_UUID)
                    val cccd = characteristic.getDescriptor(cccdUuid)
                    if (cccd == null) {
                        status = "Notification descriptor not found"
                        return
                    }

                    cccd.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                    g.writeDescriptor(cccd)

                    status = "Receiving data..."
                }


                @@ -91,86 +111,132 @@ fun CalibrationScrn(
                g: BluetoothGatt,
                characteristic: BluetoothGattCharacteristic
                ) {
                    if (characteristic.uuid == CHAR_UUID) {
                        val text = characteristic.value.toString(Charset.forName("UTF-8"))
                        val parts = text.split(",")
                        if (characteristic.uuid != charUuid) return

                        val text = characteristic.value.toString(Charset.forName("UTF-8")).trim()
                        val parsed = parseIncomingPayload(text)

                        if (parsed == null) {
                            status = "Bad data: $text"
                            return
                        }

                        roll = parts.getOrNull(0) ?: "--"
                        pitch = parts.getOrNull(1) ?: "--"
                        posture = parts.getOrNull(2) ?: "--"
                        roll = String.format("%.2f", parsed.roll)
                        pitch = String.format("%.2f", parsed.pitch)
                        posture = parsed.posture?.toString() ?: "--"
                        scoreText = String.format("%.2f", parsed.score)

                        val now = System.currentTimeMillis()
                        if (now - lastSavedMillis >= 2000L) {
                            lastSavedMillis = now
                            storage.appendSample(
                                PostureSample(
                                    timestamp = now,
                                    roll = parsed.roll,
                                    pitch = parsed.pitch,
                                    posture = parsed.posture,
                                    score = parsed.score
                                )
                            )
                            savedCount += 1
                            lastSavedTime = storage.latestSavedTimeText()
                        }
                    }
                }
            }

            // === CONNECT ON FIRST COMPOSITION ===
            LaunchedEffect(device) {
                gatt = device.connectGatt(context, false, gattCallback)
            }

            // === DISCONNECT ===
            @SuppressLint("MissingPermission")
            fun disconnect() {
                gatt?.disconnect()
                gatt?.close()
                gatt = null
                bt.isConnected = false
                bt.connectedDevice = null
                status = "Disconnected"
                onDisconnect()
            }

            Column(
            modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (showHome) {
                    androidx.compose.material3.IconButton(onClick = { navController.navigate("screen_3") }) {
                        androidx.compose.material3.Icon(
                            imageVector = Icons.Filled.Home,
                            contentDescription = "Home"
                        )
                    }
                }

                Text("Calibration", style = MaterialTheme.typography.headlineSmall)

                Column(
                    modifier = Modifier.fillMaxSize().padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                            OutlinedButton(
                            onClick = { disconnect() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF03DAC5).copy(alpha = 0.15f)),
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(2.dp, Color(0xFF009688).copy(alpha = 0.4f)),
                    elevation = null,
                    contentPadding = PaddingValues(16.dp)
                ) {
                    /*if (showArrow){
                    IconButton(onClick = { navController.navigate("screen_3") }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Localized description"
                        )

                    }*/
                    if (showHome){
                        IconButton(onClick = { navController.navigate("screen_3") }) {
                            Icon(
                                imageVector = Icons.Filled.Home,
                                contentDescription = "Localized description"
                            )
                            Text("Disconnect", color = Color.DarkGray)
                        }

                    }
                }
                else null
                Text("Calibration", style = MaterialTheme.typography.headlineSmall)

                OutlinedButton(onClick = { disconnect() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF03DAC5).copy(alpha = 0.15f)),
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(2.dp, Color(0xFF009688).copy(alpha = 0.4f)),
                    //elevation = ButtonDefaults.buttonElevation(8.dp),
                    elevation = null,
                    contentPadding = PaddingValues(16.dp)
                ) {
                    Text("Disconnect", color = Color.DarkGray)
                }
                /* Button(onClick = { navController.navigate("screen_3")},
                     colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF03DAC5).copy(alpha = 0.15f)),
                     shape = RoundedCornerShape(20.dp),
                     border = BorderStroke(2.dp, Color(0xFF009688).copy(alpha = 0.4f)),
                     //elevation = ButtonDefaults.buttonElevation(8.dp),
                     elevation = null,
                     contentPadding = PaddingValues(16.dp)
                 ) {
                     Text("Home", color = Color.DarkGray)
                 }*/

                Text("Status: $status")
                Spacer(Modifier.height(8.dp))

                Text("Roll: $roll°", style = MaterialTheme.typography.headlineMedium)
                Text("Pitch: $pitch°", style = MaterialTheme.typography.headlineMedium)
                Text("Posture: $posture", style = MaterialTheme.typography.headlineMedium)
                OutlinedButton(
                    onClick = { navController.navigate("posture_history") },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF03DAC5).copy(alpha = 0.15f)),
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(2.dp, Color(0xFF009688).copy(alpha = 0.4f)),
                    elevation = null,
                    contentPadding = PaddingValues(16.dp)
                ) {
                    Text("View Today\'s Graph", color = Color.DarkGray)
                }

                Text("Status: $status")
                Spacer(Modifier.height(8.dp))

                Text("Roll: $roll°", style = MaterialTheme.typography.headlineMedium)
                Text("Pitch: $pitch°", style = MaterialTheme.typography.headlineMedium)
                Text("Posture: $posture", style = MaterialTheme.typography.headlineMedium)
                Text("Deviation score: $scoreText", style = MaterialTheme.typography.headlineSmall)
                Text("Saved samples on phone: $savedCount", style = MaterialTheme.typography.bodyLarge)
                Text("Last saved: $lastSavedTime", style = MaterialTheme.typography.bodyLarge)
            }
        }

        private data class ParsedBleReading(
            val roll: Float,
            val pitch: Float,
            val posture: Int?,
            val score: Float
        )

        private fun parseIncomingPayload(text: String): ParsedBleReading? {
            val parts = text.split(",")
                .map { it.trim() }
                .filter { it.isNotEmpty() }

            return when {
                parts.size >= 3 -> {
                    val roll = parts[0].toFloatOrNull() ?: return null
                    val pitch = parts[1].toFloatOrNull() ?: return null
                    val posture = parts[2].toIntOrNull()
                    val score = sqrt((roll * roll) + (pitch * pitch))
                    ParsedBleReading(roll = roll, pitch = pitch, posture = posture, score = score)
                }

                parts.size == 1 -> {
                    val score = parts[0].toFloatOrNull() ?: return null
                    ParsedBleReading(roll = 0f, pitch = 0f, posture = null, score = score)
                }

                else -> null
            }
        }