package com.test.bluetoothtest.bluetooth

import android.bluetooth.BluetoothDevice

class BluetoothClient(device: BluetoothDevice): Thread() {
    private val socket = device.createRfcommSocketToServiceRecord(uuid)

    override fun run() {
        android.util.Log.i("client", "Connecting")
        this.socket.connect()

        android.util.Log.i("client", "Sending")
        val outputStream = this.socket.outputStream
        val inputStream = this.socket.inputStream
        try {
            outputStream.write("testing".toByteArray())
            outputStream.flush()
            android.util.Log.i("client", "Sent")
        } catch(e: Exception) {
            android.util.Log.e("client", "Cannot send", e)
        } finally {
            outputStream.close()
            inputStream.close()
            this.socket.close()
        }
    }
}