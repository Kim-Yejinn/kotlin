package com.example.wifidirectconnect

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wifidirectconnect.databinding.FragmentListBinding


class ListFragment : Fragment() {

    // main을 부른다
    var mainActivity:MainActivity? = null
    // 바인딩을 가져온다
    lateinit var binding:FragmentListBinding

    
    // 뷰 생성시
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

//        여기에 함수를 넣는것인가?
    ): View? {
        binding = FragmentListBinding.inflate(inflater, container, false)
        // 바인딩 설정하자
        // 화면상에 보여줄 것을 여기서 처리해야 한다.
        val data:MutableList<WiFiDirect> = loadData()
        val adapter = CustomAdapter()

        adapter.listData  = data

        binding.wifiListView.adapter = adapter
        binding.wifiListView.layoutManager = LinearLayoutManager(mainActivity)


        return binding.root
    }


    // main을 받아올때는 커밋이후 바로 실행되는 onAttach가 적합
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }
    private fun loadData():MutableList<WiFiDirect>{
        val data: MutableList<WiFiDirect> = mutableListOf()

        for(no in 1..100){
            val name = "test"
            var wifidirect = WiFiDirect(name)
            data.add(wifidirect)
        }
        return data
    }
}