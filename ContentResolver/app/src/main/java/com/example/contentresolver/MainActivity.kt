package com.example.contentresolver

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.example.contentresolver.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    lateinit var storagePermission: ActivityResultLauncher<String>
    val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        storagePermission = registerForActivityResult(
            ActivityResultContracts.RequestPermission()){
                isGranted ->
            if(isGranted){
                startProcess()
            }else{
                Toast.makeText(baseContext,
                    "외부 저장소 권한을 승인해야 앱을 사용할 수 있습니다.",
                    Toast.LENGTH_LONG).show()
                finish()
            }
        }

    }

    fun startProcess(){

    }

    fun getMusicList():List<Music>{
        val listUrl = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

        val proj = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.DURATION
        )
    }

}