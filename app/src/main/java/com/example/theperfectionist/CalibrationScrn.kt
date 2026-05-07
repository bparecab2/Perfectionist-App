package com.example.theperfectionist

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothProfile
import android.os.Handler
import android.os.Looper
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import java.nio.charset.Charset
import java.util.UUID

object BleLiveData {
    var status by mutableStateOf("Disconnected")
    var roll by mutableStateOf("--")
    var pitch by mutableStateOf("--")
    var postureStateText by mutableStateOf("--")
    var familyText by mutableStateOf("--")
    var calibrated by mutableStateOf(false)

    var connectedGatt: BluetoothGatt? = null
    var dataCharacteristic: BluetoothGattCharacteristic? = null
    var commandCharacteristic: BluetoothGattCharacteristic? = null
}

data class ParsedBleReading(
    val roll: Float,
    val pitch: Float,
    val family: String,
    val postureState: String,
    val calibrated: Boolean
)

fun parseIncomingPayload(text: String): ParsedBleReading? {
    val parts = text.split(",")
        .map { it.trim() }
        .filter { it.isNotEmpty() }

    if (parts.size < 5) return null

    val roll = parts[0].toFloatOrNull() ?: return null
    val pitch = parts[1].toFloatOrNull() ?: return null

    val family = when (parts[2]) {
        "ST" -> "STAND"
        "SI" -> "SIT"
        else -> parts[2]
    }

    val postureState = when (parts[3]) {
        "G" -> "GOOD"
        "BP" -> "BAD_PENDING"
        "VB" -> "VIBRATING"
        "CD" -> "COOLDOWN"
        else -> parts[3]
    }

    val calibrated = when (parts[4]) {
        "CAL" -> true
        "UNCAL" -> false
        else -> false
    }

    return ParsedBleReading(
        roll = roll,
        pitch = pitch,
        family = family,
        postureState = postureState,
        calibrated = calibrated
    )
}

@SuppressLint("MissingPermission")
fun sendBleCommand(command: String): Boolean {
    val gatt = BleLiveData.connectedGatt ?: return false
    val characteristic = BleLiveData.commandCharacteristic ?: return false

    characteristic.value = command.toByteArray()
    return gatt.writeCharacteristic(characteristic)
}

@SuppressLint("MissingPermission")
@Composable
fun CalibrationScrn(
    device: BluetoothDevice,
    navController: NavController
) {
    val context = LocalContext.current
    val storage = remember { PostureStorage(context) }

    val serviceUuid = UUID.fromString("12345678-1234-1234-1234-1234567890ab")
    val dataCharUuid = UUID.fromString("abcdefab-1234-1234-1234-abcdefabcdef")
    val commandCharUuid = UUID.fromString("fedcba98-4321-4321-4321-fedcba987654")
    val cccdUuid = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")

    val activity = context as MainActivity
    val bt = activity.btManager

    var gatt by remember { mutableStateOf<BluetoothGatt?>(null) }
    var hasNavigated by remember { mutableStateOf(false) }
    var receivedData by remember { mutableStateOf(false) }
    var lastSavedMillis by remember { mutableLongStateOf(0L) }
    var disconnectNotificationSent by remember { mutableStateOf(false) }

    val mainHandler = remember { Handler(Looper.getMainLooper()) }

    fun navigateOnce(route: String) {
        if (hasNavigated) return

        hasNavigated = true

        mainHandler.post {
            navController.navigate(route) {
                popUpTo("Bluetooth") { inclusive = false }
            }
        }
    }

    fun updateLiveData(parsed: ParsedBleReading) {
        mainHandler.post {
            BleLiveData.roll = String.format("%.2f", parsed.roll)
            BleLiveData.pitch = String.format("%.2f", parsed.pitch)
            BleLiveData.familyText = parsed.family
            BleLiveData.postureStateText = parsed.postureState
            BleLiveData.calibrated = parsed.calibrated
        }
    }

    val gattCallback = remember {
        object : BluetoothGattCallback() {

            override fun onConnectionStateChange(
                g: BluetoothGatt,
                statusCode: Int,
                newState: Int
            ) {
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    mainHandler.post {
                        BleLiveData.status = "Connected. Discovering services..."
                        bt.isConnected = true
                        bt.connectedDevice = device
                        BleLiveData.connectedGatt = g
                        disconnectNotificationSent = false
                    }

                    g.discoverServices()
                } else {
                    mainHandler.post {
                        BleLiveData.status = "Disconnected"
                        BleLiveData.roll = "--"
                        BleLiveData.pitch = "--"
                        BleLiveData.postureStateText = "--"
                        BleLiveData.familyText = "--"
                        BleLiveData.calibrated = false
                        BleLiveData.connectedGatt = null
                        BleLiveData.dataCharacteristic = null
                        BleLiveData.commandCharacteristic = null

                        bt.isConnected = false
                        bt.connectedDevice = null

                        if (!disconnectNotificationSent) {
                            disconnectNotificationSent = true
                            PostureNotificationHelper.showBluetoothDisconnectedNotification(context)
                        }
                    }
                }
            }

            override fun onServicesDiscovered(
                g: BluetoothGatt,
                statusCode: Int
            ) {
                val service = g.getService(serviceUuid)

                if (service == null) {
                    mainHandler.post { BleLiveData.status = "Service not found" }
                    navigateOnce("target_date/${device.address}")
                    return
                }

                val dataCharacteristic = service.getCharacteristic(dataCharUuid)

                if (dataCharacteristic == null) {
                    mainHandler.post { BleLiveData.status = "Data characteristic not found" }
                    navigateOnce("target_date/${device.address}")
                    return
                }

                val commandCharacteristic = service.getCharacteristic(commandCharUuid)

                if (commandCharacteristic == null) {
                    mainHandler.post { BleLiveData.status = "Command characteristic not found" }
                    navigateOnce("target_date/${device.address}")
                    return
                }

                mainHandler.post {
                    BleLiveData.dataCharacteristic = dataCharacteristic
                    BleLiveData.commandCharacteristic = commandCharacteristic
                    BleLiveData.status = "Waiting for data..."
                }

                g.setCharacteristicNotification(dataCharacteristic, true)

                val cccd = dataCharacteristic.getDescriptor(cccdUuid)

                if (cccd == null) {
                    mainHandler.post { BleLiveData.status = "Notification descriptor not found" }
                    navigateOnce("target_date/${device.address}")
                    return
                }

                cccd.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                g.writeDescriptor(cccd)
            }

            override fun onCharacteristicChanged(
                g: BluetoothGatt,
                characteristic: BluetoothGattCharacteristic
            ) {
                if (characteristic.uuid != dataCharUuid) return

                val text = characteristic.value
                    .toString(Charset.forName("UTF-8"))
                    .trim()

                val parsed = parseIncomingPayload(text)

                if (parsed == null) {
                    mainHandler.post { BleLiveData.status = "Bad data" }
                    navigateOnce("target_date/${device.address}")
                    return
                }

                receivedData = true
                updateLiveData(parsed)

                val now = System.currentTimeMillis()

                if (now - lastSavedMillis >= 2000L) {
                    lastSavedMillis = now

                    storage.appendSample(
                        PostureSample(
                            timestamp = now,
                            roll = parsed.roll,
                            pitch = parsed.pitch,
                            family = parsed.family,
                            postureState = parsed.postureState
                        )
                    )
                }

                if (parsed.calibrated) {
                    sendBleCommand("L")
                    navigateOnce("screen_3")
                } else {
                    navigateOnce("target_date/${device.address}")
                }
            }
        }
    }

    LaunchedEffect(device.address) {
        PostureNotificationHelper.createChannel(context)

        if (BleLiveData.connectedGatt == null) {
            gatt = device.connectGatt(context, false, gattCallback)
        } else {
            gatt = BleLiveData.connectedGatt
        }
    }

    LaunchedEffect(Unit) {
        delay(3000)

        if (!receivedData && !hasNavigated) {
            navigateOnce("target_date/${device.address}")
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppThemeState.backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Connecting to Perfectionist...",
            modifier = Modifier.padding(24.dp),
            style = MaterialTheme.typography.headlineSmall,
            color = AppThemeState.textColor
        )
    }
}