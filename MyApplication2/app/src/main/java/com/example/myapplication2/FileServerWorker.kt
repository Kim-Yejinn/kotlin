package com.example.myapplication2

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import java.io.IOException
import java.net.ServerSocket
import java.net.Socket


// 파일 전송을 위한 1:1 소켓
// 가장 최신의 기기만 연결함
class FileServerWorker(context: Context,
                       workerParams: WorkerParameters) : Worker(context, workerParams) {


    private lateinit var serverSocket:ServerSocket
    private var isRunning = false
    private val port = 8000
    private val APK_SAVE_PATH = ""

    override fun doWork(): Result {
        try{
            serverSocket = ServerSocket(port)
            isRunning = true

            while(isRunning){
                Log.d("file", "file socket connect")
                val clientSocket: Socket = serverSocket.accept()
                Log.d("file", "file start")

                Thread{
                    handleClient(clientSocket)
                }.start()
            }
        }catch (e: IOException){
            Log.e("file", e.message.toString())
            return Result.failure()
        } finally {
            return Result.success()
        }
    }
    private fun handleClient(clientSocket: Socket){
        try{
            // 값을 받을 때

    // 생각해보니까 파일을 받을 필요가 없엉..


        }catch(e: IOException){
            Log.e("file", e.message.toString())
        }
    }
}