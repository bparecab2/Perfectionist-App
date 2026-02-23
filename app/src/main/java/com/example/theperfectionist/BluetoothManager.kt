package com.example.theperfectionist

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import java.io.IOException
import java.util.UUID

class BluetoothManager12(private val context: Context) {

    private val bluetoothAdapter: BluetoothAdapter by lazy {
        val manager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        manager.adapter
    }

    // Android 12+ permission check
    private fun hasBtPermission(): Boolean {
        return context.checkSelfPermission(android.Manifest.permission.BLUETOOTH_CONNECT) ==
                PackageManager.PERMISSION_GRANTED
    }

    fun getPairedDevices(): Set<BluetoothDevice>? {
        if (!hasBtPermission()) return null
        return bluetoothAdapter.bondedDevices
    }

    fun connectToDevice(device: BluetoothDevice, onConnected: (BluetoothSocket) -> Unit) {
        if (!hasBtPermission()) return

        Thread {
            try {
                val uuid: UUID = device.uuids[0].uuid
                val socket = device.createRfcommSocketToServiceRecord(uuid)

                bluetoothAdapter.cancelDiscovery()
                socket.connect()

                Log.d("BT", "Connected to ${device.name}")
                onConnected(socket)

            } catch (e: IOException) {
                Log.e("BT", "Connection failed", e)
            }
        }.start()
    }

    fun listenForMessages(socket: BluetoothSocket) {
        if (!hasBtPermission()) return

        val input = socket.inputStream

        Thread {
            val buffer = ByteArray(1024)
            while (true) {
                val bytes = input.read(buffer)
                val msg = String(buffer, 0, bytes)
                Log.d("BT", "Received: $msg")
            }
        }.start()
    }

    fun send(socket: BluetoothSocket, msg: String) {
        if (!hasBtPermission()) return
        socket.outputStream.write(msg.toByteArray())
    }
}
