package com.example.permission

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.example.permission.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    lateinit var activityResult:ActivityResultLauncher<String>
    lateinit var callResult:ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        activityResult = registerForActivityResult(ActivityResultContracts.RequestPermission()){
            if(it){
                startProcess()
            }else{
                finish()
            }
        }

        callResult = registerForActivityResult(ActivityResultContracts.RequestPermission()){
            if(it){
                callProcess()
            }else{
                finish()
            }
        }

        binding.btnCamera.setOnClickListener{
            activityResult.launch(Manifest.permission.CAMERA)
        }

        binding.btnCall.setOnClickListener{
            callResult.launch(Manifest.permission.READ_CONTACTS)
        }



    }

    fun startProcess(){
        Toast.makeText(this, "카메라를 실행합니다.", Toast.LENGTH_LONG).show()
    }
    fun callProcess(){
        Toast.makeText(this, "연락처에 접근합니당", Toast.LENGTH_LONG).show()
    }

}
