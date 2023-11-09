package com.example.myapplication2

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import androidx.work.Worker
import androidx.work.WorkerParameters
import java.io.IOException
import java.io.InputStream
import java.net.ServerSocket
import java.net.Socket

class MsgServerWorker(context: Context,
                      workerParams: WorkerParameters) :
    Worker(context, workerParams) {

    private lateinit var serverSocket:ServerSocket
    private var isRunning = false
    private val port = 8888
    private lateinit var socketViewModel:SocketViewModel
//    private lateinit var socketList: Map<, >
    private var socketList: MutableList<Socket> = mutableListOf()

    // worker에서 실시할 작업
    override fun doWork(): Result {
        socketViewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(applicationContext as Application).create(SocketViewModel::class.java)
        try {
            serverSocket = ServerSocket(port)
            isRunning = true

            while(isRunning) {
                Log.d("chat", "chat socket connect")
                val clientSocket: Socket = serverSocket.accept()
                Log.d("chat", "chat start")
                socketList.add(clientSocket)

                Thread {
                    handleClient(clientSocket)
                }.start()
            }
        } catch (e: IOException) {
            Log.e("chat", e.message.toString())
            return Result.failure()
        } finally {
            return Result.success()
        }
    }

    private fun handleClient(clientSocket: Socket) {
        try {
            val inputStream: InputStream = clientSocket.getInputStream()
            val buffer = ByteArray(1024)

            while (true) {
                val bytesRead = inputStream.read(buffer)
                if (bytesRead == -1) {
                    // 클라이언트 연결이 종료됨
                    break
                }

                val receivedData = String(buffer, 0, bytesRead)
                Log.d("chat", "응답 $receivedData")
                
                // 받았으면 response에다가 표시

                // 연결된 곳 다 보내기
                for (i in 1..socketList.size){
                    socketList[i-1].getOutputStream().write(receivedData.toByteArray())
                }
                socketViewModel.setResData(receivedData)
            }
        } catch (e: IOException) {
            Log.e("chat", e.message.toString())
        }
    }

    fun stopServer(){
        isRunning = false
        try{
            serverSocket.close()
        }catch(e:IOException){
            Log.e("chat", e.toString())
        }
    }
}

