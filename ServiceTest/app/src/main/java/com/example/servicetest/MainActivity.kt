package com.example.servicetest

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.servicetest.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {


    // 위에 뜨는 것은 sym_def_app_icon
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }

    // 서비스 호출 함수
    fun serviceStart(view:View){
        val intent = Intent(this, MyService::class.java)
        intent.action = MyService.ACTION_START
        startService(intent)
    }
    
    // 서비스 호출
    // 서비스가 새로 생기지는 않고, onstartcomment만 불러온다
    fun serviceStop(view: View){
        val intent = Intent(this, MyService::class.java)
        stopService(intent)
        
    }


    // 서비스 커넥션
    var myService:MyService? = null
    var isService = false

    val connection = object:ServiceConnection{
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder =service as MyService.MyBinder
            myService = binder.getService()
            isService = true
            Log.d("Bound Service", "연결되었습니다.")
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            isService = false
        }
    }

    fun serviceBind(view: View){
        val intent = Intent(this, MyService::class.java)
        bindService(intent, connection, Context.BIND_AUTO_CREATE)
    }

    fun serviceUnbind(view: View){
        if (isService){
            unbindService(connection)
            isService = false
        }
    }

    fun callServiceFunction(view: View){
        if(isService){
            val message = myService?.serviceMessage()
            Toast.makeText(this, "message = ${message}", Toast.LENGTH_SHORT).show()
        }else{
            Toast.makeText(this, "서비스가 연결되지 않았습니다.", Toast.LENGTH_SHORT).show()
        }
    }

}