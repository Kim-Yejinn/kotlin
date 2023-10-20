package com.example.recyclerviewtest

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.recyclerviewtest.databinding.ItemRecyclerBinding
import java.text.SimpleDateFormat

class CustomAdapter:RecyclerView.Adapter<Holder>(){

    var listData = mutableListOf<Memo>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
//        화면에 그려지는 아이템 개수만큼 레이아웃 생성
        val binding = ItemRecyclerBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return Holder(binding)


    }

    override fun getItemCount(): Int {
//        목록에 보여줄 아이템 개수

        return listData.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
//        생성된 아이템 레이아웃에 값 입력 후 목록 출력
        val memo = listData.get(position)
        holder.setMemo(memo)
    }
}

class Holder(val binding:ItemRecyclerBinding):RecyclerView.ViewHolder(binding.root){
    fun setMemo(memo:Memo){
        binding.textNo.text = "${memo.no}"
        binding.textTitle.text = memo.title
        var sdf = SimpleDateFormat("yyyy/MM/dd")
        var formattedDate = sdf.format(memo.timestamp)
        binding.textDate.text = formattedDate

    }


}