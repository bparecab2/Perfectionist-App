package com.example.theperfectionist

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import java.io.IOException
import java.util.UUID

class BluetoothManager(private val context: Context) {

    private val bluetoothAdapter: BluetoothAdapter by lazy {
        val manager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        manager.adapter
    }

    private fun hasBtPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            context.checkSelfPermission(android.Manifest.permission.BLUETOOTH_CONNECT) ==
                    PackageManager.PERMISSION_GRANTED &&
                    context.checkSelfPermission(android.Manifest.permission.BLUETOOTH_SCAN) ==
                    PackageManager.PERMISSION_GRANTED
        } else true
    }

    @SuppressLint("MissingPermission")
    fun getPairedDevices(): Set<BluetoothDevice>? {
        if (!hasBtPermission()) return null
        return bluetoothAdapter.bondedDevices
    }

    @SuppressLint("MissingPermission")
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

    @SuppressLint("MissingPermission")
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

    @SuppressLint("MissingPermission")
    fun send(socket: BluetoothSocket, msg: String) {
        if (!hasBtPermission()) return
        socket.outputStream.write(msg.toByteArray())
    }

    @SuppressLint("MissingPermission")
    fun startDiscovery(onDeviceFound: (BluetoothDevice) -> Unit) {
        if (!hasBtPermission()) return

        val filter = android.content.IntentFilter(BluetoothDevice.ACTION_FOUND)

        val receiver = object : android.content.BroadcastReceiver() {
            override fun onReceive(context: Context, intent: android.content.Intent) {
                if (intent.action == BluetoothDevice.ACTION_FOUND) {
                    val device: BluetoothDevice? =
                        intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                    if (device != null) {
                        onDeviceFound(device)
                    }
                }
            }
        }

        context.registerReceiver(receiver, filter)
        bluetoothAdapter.startDiscovery()
    }

    @SuppressLint("MissingPermission")
    fun startBleScan(onDeviceFound: (BluetoothDevice) -> Unit) {
        if (!hasBtPermission()) return

        val scanner = bluetoothAdapter.bluetoothLeScanner

        val callback = object : android.bluetooth.le.ScanCallback() {
            override fun onScanResult(callbackType: Int, result: android.bluetooth.le.ScanResult) {
                onDeviceFound(result.device)
            }
        }

        scanner.startScan(callback)
    }
}
