package com.example.myapplication2

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.net.InetSocketAddress
import java.net.Socket


class MsgSendWorker (context: Context,
                     workerParams: WorkerParameters) : Worker(context, workerParams){


    // 필요한 변수들 정의
    private val SOCKET_TIMEOUT = 5000
    private lateinit var socketViewModel:SocketViewModel
    private lateinit var socket:Socket
    override fun doWork(): Result {
        // Worker에 전달된 데이터 키워드로 가져옴
        val host = inputData.getString(MainActivity.EXTRAS_GROUP_OWNER_ADDRESS) ?: return Result.failure()
        val port = inputData.getInt(MainActivity.EXTRAS_GROUP_OWNER_PORT, 0)
        var dataToSend = inputData.getString(MainActivity.SEND_TEXT)?:return Result.failure()

        val myApp = applicationContext as App
        socket = Socket()

        socketViewModel = myApp.socketViewModel

        // 소켓 연결
        Log.d("chat", "클라이언트 소켓 열기 - ")
        socket.bind(null)
        socket.connect(InetSocketAddress(host, port), SOCKET_TIMEOUT)
        Log.d("chat", "클라이언트 소켓 - " + socket.isConnected)

        // 송신/수신
        val outputStream: OutputStream = socket.getOutputStream()
        val inputStream: InputStream = socket.getInputStream()

        try{
            outputStream.write(dataToSend.toByteArray())

            val buffer = ByteArray(1024)
            // 값을 받아오자
            while(true){
                val bytesRead = inputStream.read(buffer)
                if (bytesRead == -1) {
                    // 클라이언트 연결이 종료됨
                    break
                }
                val receivedData = String(buffer, 0, bytesRead)
                Log.d("chat", "응답 - $receivedData")

                socketViewModel.setResData(receivedData)
            }
        } catch (e: IOException){
            Log.e("chat", e.toString())
            return Result.failure()
        } finally {
            return Result.success()
        }
    }
}