package com.example.fragment

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.fragment.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    // binding추가
    val binding by lazy { ActivityMainBinding.inflate(layoutInflater)}

    // 프래그먼트 가져옴
    lateinit var listFragment:ListFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
        setContentView(binding.root)
        setFragment()

        // 버튼 시 바인딩
        binding.btnSend.setOnClickListener{
            listFragment.setValue("전달값")
        }
    }

    
    //  프래그먼트 삽임
    fun setFragment(){
        // 트랜젝션 시작 -> 프래그먼트 추가 -> 트랜젝션 커밋

        // 프래그먼트 생성
        listFragment = ListFragment()

        // 값 전달은 argument를 사용한다
        // 번들을 생성해서 내가 넣을 값을 설정한다.
        var bundle = Bundle()
        bundle.putString("key1", "List Fragment")
        bundle.putInt("key2", 20210101)

        listFragment.arguments = bundle

        // 트랜젝션 시작
        val transaction = supportFragmentManager.beginTransaction()

        // 프래그먼트 삽입
        transaction.add(R.id.frameLayout, listFragment)

        // 커밋
        transaction.commit()

    }

    // 이동은 이쪽에 작성한다
    fun goDetail(){
        // 메서드 안에 프레그 먼트 생성
        val detailFragment = DetailFragment()

        // 트랜젝션 생성함
        val transaction = supportFragmentManager.beginTransaction()
        transaction.add(R.id.frameLayout, detailFragment)

        // 사용할 버튼
        transaction.addToBackStack("detail")

        transaction.commit()
    }

    // 코드는 다른곳에 있지만 main에 다 작성한다..?
    fun goBack(){
        // 뒤로가기가 필요할때 기본 메서드
        onBackPressed()
    }
}