package com.example.myapplication2

import android.net.wifi.p2p.WifiP2pDevice
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
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
        holder.setItem(position,info)
    }


}

class Holder(val binding: ItemRecyclerBinding):RecyclerView.ViewHolder(binding.root){

    private lateinit var device : WifiP2pDevice
    init{

        binding.root.setOnClickListener{
            Toast.makeText(binding.root.context, "클릭된 아이템 =${binding.textNo.text} / ${binding.textName.text}", Toast.LENGTH_LONG ).show()
            val mainActivity = binding.root.context as MainActivity
            mainActivity.wifiConnect(device)
        }
    }

    fun setItem(no:Int, info: WifiP2pDevice){
        device = info
        binding.textNo.text = "${no}"
        binding.textMac.text = info.deviceAddress
        binding.textName.text = info.deviceName
    }
}