package com.test.bluetoothtest.viewmodel

import android.app.Application
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.test.bluetoothtest.bluetooth.BluetoothClient
import com.test.bluetoothtest.bluetooth.BluetoothServerController
import com.test.bluetoothtest.model.BluetoothDeviceData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlin.collections.ArrayList


class MainViewModel(application: Application) : AndroidViewModel(application){
    private val viewModelJob = SupervisorJob()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)
    private val bluetoothManager = application.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    private val bluetoothAdapter = bluetoothManager.adapter
    private val pairedDevices: Set<BluetoothDevice>? = bluetoothAdapter?.bondedDevices
    private lateinit var bluetoothEnable: MutableLiveData<Boolean>
    private lateinit var bluetoothDevices: MutableLiveData<ArrayList<BluetoothDeviceData>>

    init {
        if(!::bluetoothEnable.isInitialized) {
            bluetoothEnable = MutableLiveData()
        }
        if(!::bluetoothDevices.isInitialized) {
            bluetoothDevices = MutableLiveData()
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    fun getBTEnable(): MutableLiveData<Boolean>{
        return bluetoothEnable
    }

    fun getBTDevices(): MutableLiveData<ArrayList<BluetoothDeviceData>> {
        return bluetoothDevices
    }

    fun addBTDevices(deviceData: BluetoothDeviceData) {
        var deviceList = bluetoothDevices.value

        if (deviceList == null) {
            deviceList = ArrayList()
        }
        val hasDevice: BluetoothDeviceData ?= deviceList.find {
            it.address == deviceData.address
        }
        if(hasDevice == null && !deviceData.deviceName.isNullOrBlank() &&
                !deviceData.address.isNullOrBlank()) {
            deviceList.add(deviceData)
        }
        bluetoothDevices.postValue(deviceList)
    }

    fun checkBTEnable() {
        bluetoothEnable.postValue(bluetoothAdapter.isEnabled)
    }

    fun startDiscovery() {
        bluetoothAdapter.startDiscovery()

    }
    fun cancelDiscovery() {
        bluetoothAdapter.cancelDiscovery()
    }

    fun getPairDevices() {
        pairedDevices?.forEach { device ->
            val deviceName = device.name
            val deviceHardwareAddress = device.address
        }
    }

    fun connectAsClient(address: String?) {
        uiScope.launch(Dispatchers.IO) {
            val device = bluetoothAdapter.getRemoteDevice(address)
            BluetoothClient(device).start()
        }
    }

    fun connectAsServer() {
        uiScope.launch(Dispatchers.IO) {
            BluetoothServerController().start()
        }
    }
}