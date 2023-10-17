package com.example.basicsyntax

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // v: 상세한 로그, d : 개발에 필요한 내용, i : 메시지, w: 경고, e : 에러메시지
        Log.d("BasicSyntax", "로그를 출력합니다. method = Log.d")

        var myName = "홍길동"
        var myAge:Int
        myAge = 27

        myAge = myAge+1
        Log.d("BasicSyntax", "myName = $myName, myAge = $myAge" )

    }

}