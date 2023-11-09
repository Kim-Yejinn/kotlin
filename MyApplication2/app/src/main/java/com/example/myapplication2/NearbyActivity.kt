package com.example.myapplication2

import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Button
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication2.databinding.ActivityNearbyBinding
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.AdvertisingOptions
import com.google.android.gms.nearby.connection.ConnectionInfo
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback
import com.google.android.gms.nearby.connection.ConnectionResolution
import com.google.android.gms.nearby.connection.ConnectionsClient
import com.google.android.gms.nearby.connection.ConnectionsStatusCodes
import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo
import com.google.android.gms.nearby.connection.EndpointDiscoveryCallback
import com.google.android.gms.nearby.connection.Payload
import com.google.android.gms.nearby.connection.PayloadCallback
import com.google.android.gms.nearby.connection.PayloadTransferUpdate
import com.google.android.gms.nearby.connection.Strategy
import java.io.File
import java.io.FileNotFoundException

// NearbyShare 연결 코드
class NearbyActivity:AppCompatActivity() {

    private lateinit var mClient : ConnectionsClient
    private lateinit var mDiscovererEndpoints : MutableMap<String, Endpoint>
    private lateinit var mPendingConnections : MutableMap<String, Endpoint>
    private lateinit var mEstablishedConnections : MutableMap<String, Endpoint>
    private var mIsConnecting : Boolean = false
    private var mIsDiscovering : Boolean = false
    private var mIsAdvertising : Boolean = false


    private lateinit var progressBar : ProgressBar
    private lateinit var btnFileSend : Button


    private val SERVICE_ID = "com.example.myapplication2"
    // Cluster -> 메쉬 구현시 사용,
    // star -> 별모양으로 연결 가능, 동영상 전송
    // point_to_point -> 1:1, 데이터 연결에만 집중할때
    private val STRATEGY = Strategy.P2P_POINT_TO_POINT

    // 파일 명
    private val FileName = MainActivity.ACTION_SEND_FILE

    private val binding by lazy { ActivityNearbyBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        progressBar = findViewById(R.id.progressBar)
        btnFileSend = findViewById(R.id.btnFileSend)

        // 클릭
        btnFileSend.setOnClickListener { onClickFileSend() }


        // 뷰모델
        var loadingViewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(application as Application).create(LoadingViewModel::class.java)
        loadingViewModel.lodingPercent.observe(this){
            binding.progressBar.setProgress(it,true)
        }
    }

    private fun onClickFileSend(){
        startAdvertising()
    }


    // 근처 기기 발견할 경우 연결 시작
    private val endpointDiscoveryCallback = object : EndpointDiscoveryCallback() {
        override fun onEndpointFound(endpointId: String, info: DiscoveredEndpointInfo) {
            // An endpoint was found. We request a connection to it.
            // 연결 되었을 경우 연결 요청을 날린다.
            Nearby.getConnectionsClient(applicationContext)
                .requestConnection(getLocalUserName(), endpointId, connectionLifecycleCallback)
                .addOnSuccessListener {
                    // We successfully requested a connection. Now both sides
                    // must accept before the connection is established.
                    // 연결 성공 했을경우
                    Log.d("Nearby", "연결 성공")
                }
                .addOnFailureListener { e ->
                    // Nearby Connections failed to request the connection.
                    // 실패했을 경우
                    Log.d("Nearby Error1", e.toString())
                }
        }

        override fun onEndpointLost(endpointId: String) {
            // A previously discovered endpoint has gone away.
            // 이전에 찾았던 연결 끊어짐
            Log.d("Nearby", "재연결 필요")
        }
    }

    // 연결 호출 시 실행되는 함수임.
    private val connectionLifecycleCallback: ConnectionLifecycleCallback =
        object : ConnectionLifecycleCallback() {
            override fun onConnectionInitiated(endpointId: String, connectionInfo: ConnectionInfo) {
                // Automatically accept the connection on both sides.
                // 연결되자마자 양쪽다 허용해줌
                Log.d("Nearby", "발견함")
                Nearby.getConnectionsClient(applicationContext).acceptConnection(endpointId, payloadCallback)

                // connect 한 정보 저장해 둔다.
                var endpoint:Endpoint = Endpoint(endpointId, connectionInfo.endpointName)
                mPendingConnections[endpointId] = endpoint
                this.onConnectionInitiated(endpointId, connectionInfo)
            }

            override fun onConnectionResult(endpointId: String, result: ConnectionResolution) {
                when (result.status.statusCode) {
                    ConnectionsStatusCodes.STATUS_OK -> {
                        // 연결 성공, 보내기 시작 또는 데이터 받기 시작
                        // 연결 성공했으면 파일 보내도록 해야 한다.
                        Log.d("Nearby", "연결 되어서 $endpointId 로 파일 보내야 됩니당.")
                        fileHandler(endpointId)
                    }
                    ConnectionsStatusCodes.STATUS_CONNECTION_REJECTED -> {
                        // 연결 거절됨
                        Log.d("Nearby", "연결 거절 됨")
                    }
                    ConnectionsStatusCodes.STATUS_ERROR -> {
                        // 연결 에러 생김
                        Log.d("Nearby", "연결 에러")
                    }
                    else -> {
                        // 나머지
                    }
                }
            }

            override fun onDisconnected(endpointId: String) {
                // We've been disconnected from this endpoint. No more data can be
                // sent or received.
                // 연결 이제 끊어짐.
                Log.d("Nearby", "이제 연결 끝났습니다.")
            }
        }


    // 주변 기기 찾기 시작
    private fun startAdvertising() {
        val advertisingOptions = AdvertisingOptions.Builder().setStrategy(STRATEGY).build()

        Nearby.getConnectionsClient(applicationContext)
            .startAdvertising(
                getLocalUserName(), SERVICE_ID, connectionLifecycleCallback, advertisingOptions
            )
            .addOnSuccessListener {
                // We're advertising!
                // 연결 가능 기기 찾자
                Log.d("Nearby", "연결 성공함")

            }
            .addOnFailureListener { e ->
                // We were unable to start advertising.
                // 찾기 시작 실패
                Log.d("Nearby Error2", e.toString())
            }
    }
    private fun getLocalUserName(): String {
        // 사용자 이름을 반환하는 로직을 구현해야 합니다.
        return "사용자 이름" // 임시로 사용자 이름을 반환하는 예제
    }

    // Callback for payload shared
    private val payloadCallback = object : PayloadCallback(){
        override fun onPayloadReceived(p0: String, p1: Payload) {
            // Not required as only sending payload, not receiving
            // 보낼때 거쳐감
            Log.e("AdversingDialog","-> onPayloadReceived")
        }

        override fun onPayloadTransferUpdate(p0: String, p1: PayloadTransferUpdate) {
            // If file shared successfully show Done, and if not then show data transferred
            // 받을때 로딩되는 정도 표시

            val loadingViewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(this as Application).create(LoadingViewModel::class.java)

            if(p1.totalBytes == 0L){
                loadingViewModel.setLoadingPercent(0)
            }else{
                var percent = (p1.bytesTransferred * 100 / p1.totalBytes).toInt()
                loadingViewModel.setLoadingPercent(percent)
            }
        }
    }

    // 파일을 가져와서 보내는 함수
    // 이름이 정해지면 거기로 보내버린닷
    private fun fileHandler(endpointId:String){
        var fileToSend: File = File(applicationContext.filesDir, FileName )
        try{
            var filePayload : Payload = Payload.fromFile(fileToSend)
            Nearby.getConnectionsClient(applicationContext).sendPayload(endpointId, filePayload)
        }catch (e : FileNotFoundException){
            Log.d("Nearby",  e.toString())
        }
    }
    class Endpoint(val id: String, val name: String) {
        companion object {
            fun create(id: String, name: String): Endpoint {
                return Endpoint(id, name)
            }
        }
        override fun equals(obj: Any?): Boolean {
            if (obj is Endpoint) {
                return id == obj.id
            }
            return false
        }
        override fun hashCode(): Int {
            return id.hashCode()
        }
        override fun toString(): String {
            return String.format("Endpoint{id=%s, name=%s}", id, name)
        }
    }
}