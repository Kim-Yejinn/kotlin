package com.example.myapplication2

import android.content.Context
import android.os.Environment
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import java.io.DataOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.net.InetSocketAddress
import java.net.Socket

class FileSendWorker(context: Context,
                     workerParams: WorkerParameters) : Worker(context, workerParams) {

    private val SOCKET_TIMEOUT = 5000
    private val port = 8000

    override fun doWork(): Result {
        val fileUri = inputData.getString(MainActivity.EXTRAS_FILE_PATH) ?: return Result.failure()
        val host = inputData.getString(MainActivity.EXTRAS_GROUP_OWNER_ADDRESS) ?: return Result.failure()

        val socket = Socket()

        // 소켓 연결
        Log.d("file", "클라이언트 소켓 열기 - ")
        socket.bind(null)
        socket.connect(InetSocketAddress(host,port), SOCKET_TIMEOUT)
        Log.d("file", "클라이언트 소켓 - " + socket.isConnected)

        // 송수신
        val outputStream: OutputStream = socket.getOutputStream()
        val inputStream: InputStream = socket.getInputStream()

        try{
            // 파일 찾기
            val apkFile = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileUri)
            val fileSize = apkFile.length()

            val dos = DataOutputStream(outputStream)
            Log.d("file", apkFile.name)
//                    나중에 헤더로 처리 해야함.
//                    dos.writeUTF(apkFile.name)
//                    dos.writeLong(fileSize)
//                    dos.writeBytes("\r\n")

            val fis = FileInputStream(apkFile)
            val buffer = ByteArray(1024)
            var bytesRead:Int
            while(fis.read(buffer).also {bytesRead = it} != -1){
                dos.write(buffer, 0, bytesRead)
            }
            fis.close()
            dos.flush()
            dos.close()

        } catch(e: IOException){
            Log.e("file", e.toString())
            return Result.failure()
        } finally {
            return Result.success()
        }



    }



}