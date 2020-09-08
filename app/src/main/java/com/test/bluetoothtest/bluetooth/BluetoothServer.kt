package com.test.bluetoothtest.bluetooth

import android.bluetooth.BluetoothSocket

class BluetoothServer(private val socket: BluetoothSocket): Thread() {
    private val inputStream = this.socket.inputStream
    private val outputStream = this.socket.outputStream

    override fun run() {
        try {
            val available = inputStream.available()
            val bytes = ByteArray(available)
            android.util.Log.i("server", "Reading")
            inputStream.read(bytes, 0, available)
            val text = String(bytes)
            android.util.Log.i("server", "Message received")
            android.util.Log.i("server", text)
        } catch (e: Exception) {
            android.util.Log.e("client", "Cannot read data", e)
        } finally {
            inputStream.close()
            outputStream.close()
            socket.close()
        }
    }
}