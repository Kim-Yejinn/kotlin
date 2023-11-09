package com.example.myapplication2

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.wifi.WifiManager
import android.net.wifi.WpsInfo
import android.net.wifi.p2p.WifiP2pConfig
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.example.myapplication2.databinding.ActivityMainBinding
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import java.io.File


class MainActivity : AppCompatActivity(){
    private lateinit var WifiManager: WifiManager
    private lateinit var mManager:WifiP2pManager
    private lateinit var mChannel: WifiP2pManager.Channel

    private lateinit var mReceiver: BroadcastReceiver
    private lateinit var mIntentFilter: IntentFilter

    private lateinit var btnConnect:Button
    private lateinit var btnDiscover:Button
    private lateinit var btnSend:Button
    private lateinit var btnStop:Button
    private lateinit var btnFile:Button
    private lateinit var btnNext:Button
    private lateinit var btnAware:Button
    private lateinit var btnShare:Button

    private lateinit var recyclerView: RecyclerView
    private lateinit var textStatus: TextView
    lateinit var textWiFiStatus: TextView
    private lateinit var writeMsg: EditText
    lateinit var connectionStatus:TextView
    lateinit var receiveMsg : TextView

    private val peers = mutableListOf<WifiP2pDevice>()
    private var listAdapter = WiFiPeerListAdapter()

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    // 소켓 관련 변수들
    private lateinit var socketHostAddress:String
    private val socketPort:Int = 8888


    private lateinit var sendWorkerManager:WorkManager

    // ui관련 변수 (뷰모델)
    private lateinit var socketViewModel: SocketViewModel
    companion object{

        val KEY_COUNT_VALUE = "test"
        val ACTION_SEND_FILE = "app-release.apk"
        val EXTRAS_FILE_PATH = "file_url"
        val EXTRAS_GROUP_OWNER_ADDRESS = "go_host"
        val EXTRAS_GROUP_OWNER_PORT = "go_port"
        val SEND_TEXT = "sendText"
        lateinit var REQUIRED_PERMISSIONS : Array<String>

    }
    init {
        REQUIRED_PERMISSIONS = when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> arrayOf(
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_ADVERTISE,
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.CHANGE_WIFI_STATE,
                Manifest.permission.NEARBY_WIFI_DEVICES,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.MANAGE_EXTERNAL_STORAGE
            )
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> arrayOf(
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_ADVERTISE,
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.CHANGE_WIFI_STATE,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.MANAGE_EXTERNAL_STORAGE
            )
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> arrayOf(
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.CHANGE_WIFI_STATE,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            else -> arrayOf(
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.CHANGE_WIFI_STATE,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        }
    }

    private val REQUEST_CODE_REQUIRED_PERMISSIONS = 1


    private lateinit var activityResult:ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // 와이파이 리시버 등록
        // 수신받을 것들

        // 버튼과 연결
        btnConnect = findViewById(R.id.btnConnect)
        btnDiscover = findViewById(R.id.btnDiscover)
        btnSend = findViewById(R.id.btnSend)
        recyclerView = findViewById(R.id.recyclerView)
        textStatus = findViewById(R.id.textStatus)
        textWiFiStatus = findViewById(R.id.textWiFiStatus)
        writeMsg = findViewById(R.id.writeMsg)
        connectionStatus = findViewById(R.id.connectionStatus)
        receiveMsg = findViewById(R.id.receiveMsg)
        btnStop = findViewById(R.id.btnStop)
        btnFile = findViewById(R.id.btnFile)
        btnNext = findViewById(R.id.btnNext)
        btnAware = findViewById(R.id.btnAware)
        btnShare = findViewById(R.id.btnShare)

        // 권한 설정
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(!arePermissionsGranted(REQUIRED_PERMISSIONS)){
                requestPermissions(REQUIRED_PERMISSIONS, REQUEST_CODE_REQUIRED_PERMISSIONS)
            }
        }

        // 구글 플레이
        var apiAvailability :GoogleApiAvailability = GoogleApiAvailability.getInstance()
        var resultCode= apiAvailability.isGooglePlayServicesAvailable(this)
        if( resultCode !=ConnectionResult.SUCCESS ){
            if(apiAvailability.isUserResolvableError(resultCode)){
                apiAvailability.getErrorDialog(this, resultCode, 9000)?.show()
            }else{
                // 지원하지 않을경우
                Toast.makeText(
                    this@MainActivity,
                    "구글도 없다니..",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        // 와이 파이 매니저로 주변 피어 검색하거나 원하는 것 찾기 가능
        mManager = getSystemService(Context.WIFI_P2P_SERVICE) as WifiP2pManager
        mChannel = mManager.initialize(this, mainLooper, null)

        mIntentFilter = IntentFilter()
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION)
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION)
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION)
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION)

        WifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        mReceiver=  WiFiDirectBroadcastReceiver(mManager, mChannel, this )

        binding.recyclerView.adapter = listAdapter
        binding.recyclerView.layoutManager =LinearLayoutManager(this)

        // 클릭 이벤트
        btnConnect.setOnClickListener{onClickConnect()}
        btnDiscover.setOnClickListener{onClickDiscover()}
        btnSend.setOnClickListener{onClickSend()}
        btnStop.setOnClickListener {onClickStop()}
        btnFile.setOnClickListener {onClickFile()}
        btnNext.setOnClickListener {onClickNext()}
        btnAware.setOnClickListener { onClickAware() }
        btnShare.setOnClickListener { onClickShare() }

        socketViewModel = (application as App).socketViewModel
        sendWorkerManager = WorkManager.getInstance(applicationContext)

        socketViewModel.responseData.observe(this){
            binding.receiveMsg.text = it.toString()
        }
    }

    private fun onClickShare() {
        val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), ACTION_SEND_FILE)
        val fileUri = FileProvider.getUriForFile(this, "com.example.myapplication2.file-provider", file)

        Log.d("send","파일 보내기")

        val sendIntent = Intent()
        sendIntent.action = Intent.ACTION_SEND
        sendIntent.putExtra(Intent.EXTRA_STREAM, fileUri)
        sendIntent.type = "application/vnd.android.package-archive"
        startActivity(sendIntent)
    }


    private fun arePermissionsGranted(permissions: Array<String>): Boolean {
        for (permission in permissions) {
            if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }


    override fun onResume() {
        super.onResume()
        mReceiver?.also { receiver ->
            registerReceiver(receiver, mIntentFilter)
        }
    }

    override fun onPause() {
        super.onPause()
        mReceiver?.also { receiver ->
            unregisterReceiver(receiver)
        }
    }

    fun wifiConnect(device:WifiP2pDevice) {
//        // Picking the first device found on the network.
//        // 클릭한 번호의 기기를 연결하자
        val config = WifiP2pConfig().apply {
            deviceAddress = device.deviceAddress
            wps.setup = WpsInfo.PBC
        }

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.NEARBY_WIFI_DEVICES
            ) != PackageManager.PERMISSION_GRANTED
        ) {
        }
        
        mManager.connect(mChannel, config, object : WifiP2pManager.ActionListener {

            override fun onSuccess() {
                Toast.makeText(this@MainActivity, "success",Toast.LENGTH_LONG).show()
                Log.d("test", "여긴가 성공")    
            
            // WiFiDirectBroadcastReceiver notifies us. Ignore for now.
            }

            override fun onFailure(reason: Int) {
                Toast.makeText(
                    this@MainActivity,
                    "Connect failed. Retry.",
                    Toast.LENGTH_SHORT
                ).show()
                Log.d("test", "여기일지도 실패")
            }
        })
    }

    val connectionListener = WifiP2pManager.ConnectionInfoListener { info ->
        // InetAddress from WifiP2pInfo struct.
        socketHostAddress = info.groupOwnerAddress.hostAddress

        // (server).
        if (info.groupFormed && info.isGroupOwner) {
            connectionStatus.text = "server"

            // 소켓을 연결한다.
            val workManager = WorkManager.getInstance(applicationContext)
            // one time work request 생성
            val connectRequest = OneTimeWorkRequest.Builder(MsgServerWorker::class.java)
                .build()
            // 실행
            workManager.enqueue(connectRequest)

        } else if (info.groupFormed) {
            // (client)
            connectionStatus.text = "client"
            // 이때도 서버에게 연결하자
        }
    }

    private fun onClickConnect(){
        // 연결 되었을 경우
        if (WifiManager.isWifiEnabled) {
            textWiFiStatus.text = "wifi on"
        }else{
            textWiFiStatus.text = "wifi off"
            // 와이파이 연결 안된 상태
            if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.Q){
                val panelIntent = Intent(Settings.ACTION_WIFI_SETTINGS)
                startActivity(panelIntent)
                textWiFiStatus.text = "wifi on"
            }
        }
    }

    private fun onClickDiscover() {

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.NEARBY_WIFI_DEVICES
            ) != PackageManager.PERMISSION_GRANTED
        ) {
        }

        mManager.discoverPeers(mChannel, object : WifiP2pManager.ActionListener {
            override fun onSuccess() {
                textStatus.text = ("Discovery Started")
            }
            override fun onFailure(reason: Int) {
                textStatus.text = ("Discovery not Started")
            }
        })
    }



    private fun onClickFile(){
        val data = Data.Builder()
            .putInt(KEY_COUNT_VALUE, 125)
            .putString(EXTRAS_FILE_PATH, ACTION_SEND_FILE)
            .putString(EXTRAS_GROUP_OWNER_ADDRESS, socketHostAddress)
            .putInt(EXTRAS_GROUP_OWNER_PORT, socketPort)
            .build()

//        // one time work request 생성
        val uploadRequest = OneTimeWorkRequest.Builder(MsgSendWorker::class.java)
            .setInputData(data)
            .build()

        // 실행
        sendWorkerManager.enqueue(uploadRequest)
        Log.d("file", ACTION_SEND_FILE)
    }
    private fun onClickSend(){
        // 클릭했을때 워크 매니저를 실행시킨다

        // worker에 넣을 데이터
        var sendStr = writeMsg.text.toString()
        // 연달아서 넣어야 됨
        val data = Data.Builder()
            .putInt(KEY_COUNT_VALUE, 125)
            .putString(EXTRAS_FILE_PATH, ACTION_SEND_FILE)
            .putString(EXTRAS_GROUP_OWNER_ADDRESS, socketHostAddress)
            .putInt(EXTRAS_GROUP_OWNER_PORT, socketPort)
            .putString(SEND_TEXT, sendStr)
            .build()
//
//        // one time work request 생성
        val uploadRequest = OneTimeWorkRequest.Builder(MsgSendWorker::class.java)
            .setInputData(data)
            .build()

        // 실행
        sendWorkerManager.enqueue(uploadRequest)
    }
    private fun onClickStop(){


    }

    private fun onClickAware() {
        // 페이지 이동
        val intent = Intent(this, WifiAwareActivity::class.java)
        startActivity(intent)
    }

    private fun onClickNext(){
        // 담페이지
        val intent = Intent(this, NearbyActivity::class.java)
        startActivity(intent)
    }


    val peerListListener = WifiP2pManager.PeerListListener { peerList ->
        val refreshedPeers = peerList.deviceList

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.NEARBY_WIFI_DEVICES
            ) != PackageManager.PERMISSION_GRANTED
        ) {
        }

        if (refreshedPeers != peers) {
            peers.clear()
            peers.addAll(refreshedPeers)

            listAdapter.listData.clear()
            listAdapter.listData.addAll(peers)

            listAdapter.notifyDataSetChanged()
            Log.d("search result","list Changed")
            Log.d("search result cnt", peers.size.toString())

        }

        if (peers.isEmpty()) {
            Log.d("find peers result", "No devices found")
            return@PeerListListener
        }
    }

}