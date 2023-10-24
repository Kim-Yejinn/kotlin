package com.example.myapplication2

import android.net.wifi.p2p.WifiP2pDevice
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication2.databinding.ItemRecyclerBinding

class WiFiPeerListAdapter : RecyclerView.Adapter<Holder>() {

    var listData = mutableListOf<WifiP2pDevice>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = ItemRecyclerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        Log.d("cnt", listData.size.toString())
        return Holder(binding)
    }

    override fun getItemCount(): Int {

        return listData.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val info = listData.get(position)
        holder.setItem(info.deviceName, info.deviceAddress)
    }


}

class Holder(val binding: ItemRecyclerBinding):RecyclerView.ViewHolder(binding.root){


    fun setItem(name:String, mac:String ){
        binding.textMac.text = mac
        binding.textName.text = name
    }
}