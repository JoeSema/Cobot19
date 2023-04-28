package com.example.cobot19

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.*

class BluetoothCommunicationStrategy(private val macAddress: String) : CommunicationStrategy {
    private var socket: BluetoothSocket? = null
    override fun sendMessage(message: String) {
        TODO("Not yet implemented")
    }


}
