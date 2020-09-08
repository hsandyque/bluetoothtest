package com.test.bluetoothtest.model

data class BluetoothDeviceData(
    val deviceName: String?,
    val address: String?
) {
    override fun equals(other: Any?): Boolean {
        val (_, address1) = other as BluetoothDeviceData

        return address == address1
    }

    override fun toString(): String {
        return address?: ""
    }
}