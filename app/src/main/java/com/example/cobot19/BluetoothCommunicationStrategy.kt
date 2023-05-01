package com.example.cobot19

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.*

class BluetoothCommunicationStrategy(
    private val context: Context,
    private val macAddress: String
) : CommunicationStrategy {

    private lateinit var bluetoothStrategy: BluetoothCommunicationStrategy
    private val handler = Handler(Looper.getMainLooper())
    private var device: BluetoothDevice? = null
    private lateinit var socket: BluetoothSocket
    private lateinit var outputStream: OutputStream
    private lateinit var inputStream: InputStream
    private val TAG = "BluetoothCommunicationStrategy"
    private val uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

    fun connect() {
        val adapter = BluetoothAdapter.getDefaultAdapter()
        if (adapter == null) {
            showToast("Bluetooth not supported")
            return
        }
        if (!adapter.isEnabled) {
            showToast("Bluetooth not enabled")
            return
        }
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                context as Activity,
                arrayOf(Manifest.permission.BLUETOOTH),
                1
            )
            return
        }
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_ADMIN
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                context as Activity,
                arrayOf(Manifest.permission.BLUETOOTH_ADMIN),
                1
            )
            return
        }
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                context as Activity,
                arrayOf(Manifest.permission.BLUETOOTH_CONNECT),
                1
            )
            return
        }

        // Get a set of currently paired devices
        val pairedDevices = adapter.bondedDevices

        // If there are paired devices, add each one to the ArrayAdapter
        if (pairedDevices.isNotEmpty()) {
            for (device in pairedDevices) {
                val deviceName = device.name
                val deviceHardwareAddress = device.address // MAC address
                Log.d(TAG, "Paired device: $deviceName ($deviceHardwareAddress)")
            }
        } else {
            Log.d(TAG, "No paired devices found")
        }

        // Find the device with the specified MAC address
        device = adapter.getRemoteDevice(macAddress)


        try {
            socket = device!!.createRfcommSocketToServiceRecord(UUID.randomUUID())
            socket.connect()
            outputStream = socket.outputStream
            inputStream = socket.inputStream
            showToast("Connected to device")
        } catch (e: IOException) {
            showToast("Failed to connect to device")
            e.printStackTrace()
        }
    }

    override fun sendMessage(message: String) {
        try {
            if (::outputStream.isInitialized) { // Check if the outputStream is initialized
                outputStream.write(message.toByteArray())
            } else {
                // Handle the case where the outputStream is not initialized
                showToast("Output stream not initialized")
            }
            if (!::inputStream.isInitialized) {
                showToast("Error sending message: inputStream not initialized")
                return
            }
            // Convert the message to bytes and write to the output stream
            val messageBytes = message.toByteArray(Charsets.UTF_8)
            outputStream.write(messageBytes)

            // Read the response from the input stream
            val buffer = ByteArray(1024)
            val bytes = inputStream.read(buffer)
            val response = String(buffer, 0, bytes, Charsets.UTF_8)
            showToast("Received response: $response")
        } catch (e: IOException) {
            showToast("Error sending message: ${e.message}")


            e.printStackTrace()
        }
    }


    private fun showToast(message: String) {
        handler.post {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }
}
