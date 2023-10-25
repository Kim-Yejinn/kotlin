package com.example.servicetest

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log

class MyService : Service() {




    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action
        Log.d("Started Service", "action = $action")
        return super.onStartCommand(intent, flags, startId)
    }

    // 테스트로 사용할 명령어
    // 명령어는 패키지명 + 명령어 조합으로 만들어 짐
    companion object{
        val ACTION_START = "com.example.servicetest.START"
        val ACTION_RUN = "com.example.servicetest.RUN"
        val ACTION_STOP = "com.example.servicetest.STOP"
    }

    // 종료시 호출
    override fun onDestroy() {
        Log.d("Service", "서비스가 종료되었습니다.")
        super.onDestroy()
    }


    // 바인더의 getService로 접근을 위함
    inner class MyBinder: Binder(){
        fun getService():MyService{
            return this@MyService
        }
    }

    val binder = MyBinder()


    // started service에서는 사용하지 않음
    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    fun serviceMessage():String{
        return "Hello Activity! I am Service!"
    }
}