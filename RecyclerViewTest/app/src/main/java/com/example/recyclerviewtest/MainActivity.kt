package com.example.recyclerviewtest

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.recyclerviewtest.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    val binding by lazy{ActivityMainBinding.inflate(layoutInflater)}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        //데이터 생성
        val data:MutableList<Memo> = loadData()

        // 어댑터 생성
        val adapter = CustomAdapter()
        
        // 값 넣구
        adapter.listData = data
        
        // 어댑터 연결
        binding.recyclerView.adapter = adapter


        // 레이아웃 매니저 연결
        binding.recyclerView.layoutManager = LinearLayoutManager(this)

    }

    private fun loadData():MutableList<Memo>{
        val data: MutableList<Memo> = mutableListOf()

        for(no in 1..100){
            val title = "test"
            val date = System.currentTimeMillis()

            var memo = Memo(no, title, date)

            data.add(memo)
        }

        return data
    }
}