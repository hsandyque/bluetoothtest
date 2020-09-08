package com.test.bluetoothtest.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.test.bluetoothtest.R
import com.test.bluetoothtest.listener.DeviceListClickListener
import com.test.bluetoothtest.model.BluetoothDeviceData
import kotlinx.android.synthetic.main.item_device_list.view.*

class DeviceListAdapter(private val listener: DeviceListClickListener):
    RecyclerView.Adapter<DeviceListAdapter.ViewHolder>() {
    private var datas: ArrayList<BluetoothDeviceData> = arrayListOf()

    fun setData(list: ArrayList<BluetoothDeviceData>) {
        datas = list
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.item_device_list,
            parent,
            false
        )
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = datas[position]
        holder.bindName(currentItem.deviceName)
        holder.bindAddress(currentItem.address)
        holder.setClickListener(currentItem.address, listener)
    }

    override fun getItemCount(): Int {
        return datas.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindName(name: String?) {
            itemView.tv_device_name.text = name
        }

        fun bindAddress(address: String?) {
            itemView.tv_device_address.text = address
        }

        fun setClickListener(address: String?, listener: DeviceListClickListener) {
            itemView.setOnClickListener {
                listener.onClick(address)
            }
        }
    }
}