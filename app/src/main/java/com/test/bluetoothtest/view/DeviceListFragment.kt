package com.test.bluetoothtest.view

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.test.bluetoothtest.R
import com.test.bluetoothtest.adapter.DeviceListAdapter
import com.test.bluetoothtest.listener.DeviceListClickListener
import com.test.bluetoothtest.model.BluetoothDeviceData
import com.test.bluetoothtest.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.fragment_device_list.*
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import pub.devrel.easypermissions.PermissionRequest


class DeviceListFragment : Fragment(), EasyPermissions.PermissionCallbacks,
    EasyPermissions.RationaleCallbacks, DeviceListClickListener {
    val RC_LOCATION = 666
    val RC_ENABLE_BT = 888

    private val mainViewModel by lazy {
        ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
    }
    private val adapter by lazy {
        DeviceListAdapter(this)
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_device_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupViewModel()
    }

    override fun onResume() {
        super.onResume()
        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        requireActivity().registerReceiver(receiver, filter)
    }

    override fun onPause() {
        super.onPause()
        stopScan()
        requireActivity().unregisterReceiver(receiver)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        mainViewModel.checkBTEnable()
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        }
    }

    override fun onRationaleAccepted(requestCode: Int) {
        mainViewModel.checkBTEnable()
    }

    override fun onRationaleDenied(requestCode: Int) {
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK && requestCode == RC_ENABLE_BT) {
            startScan()
        }
    }

    private fun setupViews() {
        fab_scan.setOnClickListener{
            requirePermission()
        }
        rv_device_list.layoutManager = LinearLayoutManager(
            requireContext(), LinearLayoutManager.VERTICAL, false
        )
        rv_device_list.adapter = adapter
        btn_server.setOnClickListener {
            mainViewModel.connectAsServer()
            btn_server.visibility = View.GONE
            btn_client.visibility = View.GONE
            btn_discoverable.visibility = View.VISIBLE
        }
        btn_client.setOnClickListener {
            btn_server.visibility = View.GONE
            btn_client.visibility = View.GONE
            rv_device_list.visibility = View.VISIBLE
            (fab_scan as View).visibility = View.VISIBLE
        }
        btn_discoverable.setOnClickListener {
            enableDiscoverable()
        }
    }
    private fun setupViewModel() {
        mainViewModel.getBTEnable().observe(viewLifecycleOwner, Observer { enable ->
            if (!enable) {
                val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startActivityForResult(enableBtIntent, RC_ENABLE_BT)
            } else {
                startScan()
            }
        })
        mainViewModel.getBTDevices().observe(viewLifecycleOwner, Observer {
            adapter.setData(it)
        })
    }
    private fun startScan() {
        mainViewModel.startDiscovery()
    }
    private fun stopScan() {
        mainViewModel.cancelDiscovery()
    }
    private fun requirePermission() {
        val perms = Manifest.permission.ACCESS_FINE_LOCATION
        if (EasyPermissions.hasPermissions(requireContext(), perms)) {
            mainViewModel.checkBTEnable()
        } else {
            EasyPermissions.requestPermissions(
                PermissionRequest.Builder(this, RC_LOCATION, perms)
                    .setRationale(R.string.location_rationale)
                    .setPositiveButtonText(R.string.action_ok)
                    .setNegativeButtonText(R.string.action_cancel)
                    .build()
            )
        }
    }
    private fun enableDiscoverable() {
        val discoverableIntent: Intent = Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE).apply {
            putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300)
        }
        startActivity(discoverableIntent)
    }
    // Create a BroadcastReceiver for ACTION_FOUND.
    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when(intent.action) {
                BluetoothDevice.ACTION_FOUND -> {
                    // Discovery has found a device. Get the BluetoothDevice
                    // object and its info from the Intent.
                    val device: BluetoothDevice? =
                        intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                    val deviceName = device?.name
                    val deviceHardwareAddress = device?.address // MAC address
                    mainViewModel.addBTDevices(
                        BluetoothDeviceData(deviceName, deviceHardwareAddress)
                    )
                }
            }
        }
    }
    override fun onClick(address: String?) {
        mainViewModel.connectAsClient(address)
    }
}