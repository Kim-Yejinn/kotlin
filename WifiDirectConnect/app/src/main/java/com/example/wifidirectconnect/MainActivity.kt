package com.example.wifidirectconnect

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wifidirectconnect.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    // 바인딩 가져옴
    val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    // 프레그 먼트 가져와야함
    lateinit var listFragment: ListFragment


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 바인딩하자
        setContentView(binding.root)
        // 프레그먼트 바로 연결
        setFragment()



    }

    fun setFragment(){
        // 프레그먼트는 트랜젝션으로 관리한다
        // 프레그먼트 생성
        listFragment = ListFragment()

        // 트렌젝션 시작
        val transaction = supportFragmentManager.beginTransaction()
        // 프레그먼트 삽입
        transaction.add(R.id.wifiListView, listFragment)
        // 커밋
        transaction.commit()
    }


}