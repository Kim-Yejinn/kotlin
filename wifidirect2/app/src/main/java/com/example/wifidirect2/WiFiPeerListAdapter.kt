package com.example.wifidirect2

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class WiFiPeerListAdapter:RecyclerView.Adapter<Holder>() {
    var listData = mutableListOf<WiFiDirect>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = ItemRecyclerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        // 총 개수 리턴
        return listData.size
    }

    override fun getItemCount(): Int {
        //출력
        val wifidirect = listData.get(position)
        holder.setWifi(wifidirect)
    }


}

class Holder(val binding:ItemRecyclerBinding):RecyclerView.ViewHolder(binding.root){

    fun setWifi(wifidirect:WiFiDirect){
        binding.textWiFi.text = wifidirect.name
    }

}