package com.example.myapplication2

import android.Manifest
import android.content.Context
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.wifi.aware.AttachCallback
import android.net.wifi.aware.DiscoverySessionCallback
import android.net.wifi.aware.PeerHandle
import android.net.wifi.aware.PublishConfig
import android.net.wifi.aware.PublishDiscoverySession
import android.net.wifi.aware.SubscribeConfig
import android.net.wifi.aware.SubscribeDiscoverySession
import android.net.wifi.aware.WifiAwareManager
import android.net.wifi.aware.WifiAwareSession
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import com.example.myapplication2.databinding.ActivityNearbyBinding

// wifi aware 테스트임.
class WifiAwareActivity : AppCompatActivity() {

    private val binding by lazy { ActivityNearbyBinding.inflate(layoutInflater) }

    private lateinit var wifiAwareManager:WifiAwareManager
    private lateinit var aIntentFilter: IntentFilter
    private lateinit var aReceiver: WifiAwareBroadcastReceiver
    private val AWARE_FILE_SHARE_SERVICE_NAME = "com.example.myapplication2"
    private lateinit var wifiAwareSession: WifiAwareSession

    private lateinit var subscribeDiscoverySession : SubscribeDiscoverySession
    private lateinit var publishDiscoverySession: PublishDiscoverySession


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val wifiAwareCheck = applicationContext.packageManager.hasSystemFeature(PackageManager.FEATURE_WIFI_AWARE)

        if(wifiAwareCheck){
            Log.d("Aware", "yes")
        }else{
            Log.d("Aware", "No")
        }


        wifiAwareManager = applicationContext.getSystemService(Context.WIFI_AWARE_SERVICE) as WifiAwareManager
        aIntentFilter = IntentFilter(WifiAwareManager.ACTION_WIFI_AWARE_STATE_CHANGED)
        aReceiver = WifiAwareBroadcastReceiver(wifiAwareManager)
        application.registerReceiver(aReceiver, aIntentFilter)

        val sessionCallback = object : AttachCallback() {
            override fun onAttached(session: WifiAwareSession?) {
                super.onAttached(session)
                if (session != null) {
                    wifiAwareSession = session
                }
                Log.d("Aware", "Aware attach")
                publish()
                Thread.sleep(1000) // give some time so we don't wind up trying to be both at once
                subscribe()
            }

            override fun onAttachFailed() {
                super.onAttachFailed()
                wifiAwareSession?.close()
                Log.d("Aware", "Aware attach failed")
            }
        }

        wifiAwareManager.attach(sessionCallback, null)
    }


    // 서비스 게시
    private fun publish(){
        val config: PublishConfig = PublishConfig.Builder()
            .setServiceName(AWARE_FILE_SHARE_SERVICE_NAME)
            .build()

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.NEARBY_WIFI_DEVICES
            ) != PackageManager.PERMISSION_GRANTED
        ) {

        }
        wifiAwareSession.publish(config, object : DiscoverySessionCallback() {

            override fun onPublishStarted(session: PublishDiscoverySession) {
                // 퍼블리시 성공시
//                session.sendMessage()
                publishDiscoverySession = session
                Log.d("Aware", "publish start")
            }

            override fun onMessageReceived(peerHandle: PeerHandle, message: ByteArray) {
                // 구독자가 알림 받을시 호출
                // peerhandle로 다시 메시지 전송가능
                Log.d("Aware", "msg received")


                // 굳이 대용량 보낼거면 여기서 소켓을 형성함.
//                serverSocket = ServerSocket(0)
//                val port = serverSocket.localPort
//
//                Thread {
//                    logcat { "attempting to accept connection at publisher" }
//                    val socket = serverSocket.accept() // blocks
//                    logcat { "connection accepted at publisher" }
//                    inputStream = socket.getInputStream()
//                    outputStream = socket.getOutputStream()
//                    readData()
//                }.start()
//
//                network(publishDiscoverySession, peerHandle, port)
                publishDiscoverySession.sendMessage(peerHandle, 0, "test".toByteArray())
            }
        }, null)
    }

    private fun subscribe(){
        val config: SubscribeConfig = SubscribeConfig.Builder()
            .setServiceName(AWARE_FILE_SHARE_SERVICE_NAME)
            .build()
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.NEARBY_WIFI_DEVICES
            ) != PackageManager.PERMISSION_GRANTED
        ) {
        }

        wifiAwareSession.subscribe(config, object : DiscoverySessionCallback() {
            override fun onSubscribeStarted(session: SubscribeDiscoverySession) {
                // 구독 성공시 호출
                subscribeDiscoverySession = session
            }

            override fun onServiceDiscovered(
                peerHandle: PeerHandle,
                serviceSpecificInfo: ByteArray,
                matchFilter: List<ByteArray>
            ) {
                var test :String= "tteesstt"
                // peer handle로 연결하거나 메시지 보낼 수 있음.
                subscribeDiscoverySession.sendMessage(peerHandle, 0, test.toByteArray())
            }

            override fun onMessageReceived(peerHandle: PeerHandle?, message: ByteArray?) {
                Log.d("Aware", "subscriber msg received")
                if (message != null) {
                    Log.d("Aware", message.toString(Charsets.UTF_8))
                }

            }
        }, null)
    }
}
