package com.example.theperfectionist

import android.annotation.SuppressLint
import android.bluetooth.*
import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import java.nio.charset.Charset
import java.util.UUID

@Composable
fun CalibrationScrn(
    device: BluetoothDevice,          // You pass this from BluetoothScrn
    onDisconnect: () -> Unit = {}     // Optional callback
) {
    val context = LocalContext.current

    // BLE UUIDs
    val SERVICE_UUID = UUID.fromString("12345678-1234-1234-1234-1234567890ab")
    val CHAR_UUID = UUID.fromString("abcdefab-1234-1234-1234-abcdefabcdef")
    val CCCD_UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")

    // BLE connection state
    var gatt by remember { mutableStateOf<BluetoothGatt?>(null) }

    // UI state
    var status by remember { mutableStateOf("Connecting...") }
    var roll by remember { mutableStateOf("--") }
    var pitch by remember { mutableStateOf("--") }
    var posture by remember { mutableStateOf("--") }

    // === GATT CALLBACK ===
    val gattCallback = remember {
        object : BluetoothGattCallback() {

            @SuppressLint("MissingPermission")
            override fun onConnectionStateChange(g: BluetoothGatt, statusCode: Int, newState: Int) {
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    status = "Connected. Discovering services..."
                    g.discoverServices()
                } else {
                    status = "Disconnected"
                }
            }

            @SuppressLint("MissingPermission")
            override fun onServicesDiscovered(g: BluetoothGatt, statusCode: Int) {
                val service = g.getService(SERVICE_UUID)
                val characteristic = service?.getCharacteristic(CHAR_UUID)

                if (characteristic == null) {
                    status = "Characteristic not found"
                    return
                }

                g.setCharacteristicNotification(characteristic, true)

                val cccd = characteristic.getDescriptor(CCCD_UUID)
                cccd.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                g.writeDescriptor(cccd)

                status = "Receiving data..."
            }

            override fun onCharacteristicChanged(
                g: BluetoothGatt,
                characteristic: BluetoothGattCharacteristic
            ) {
                if (characteristic.uuid == CHAR_UUID) {
                    val text = characteristic.value.toString(Charset.forName("UTF-8"))
                    val parts = text.split(",")

                    roll = parts.getOrNull(0) ?: "--"
                    pitch = parts.getOrNull(1) ?: "--"
                    posture = parts.getOrNull(2) ?: "--"
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
        status = "Disconnected"
        onDisconnect()
    }

    // === UI ===
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Calibration", style = MaterialTheme.typography.headlineSmall)

        OutlinedButton(onClick = { disconnect() }) {
            Text("Disconnect")
        }

        Text("Status: $status")
        Spacer(Modifier.height(8.dp))

        Text("Roll: $roll°", style = MaterialTheme.typography.headlineMedium)
        Text("Pitch: $pitch°", style = MaterialTheme.typography.headlineMedium)
        Text("Posture: $posture", style = MaterialTheme.typography.headlineMedium)
    }
}
