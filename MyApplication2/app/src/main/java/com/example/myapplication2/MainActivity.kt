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
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication2.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(){
    private lateinit var WifiManager: WifiManager
    private lateinit var mManager:WifiP2pManager
    private lateinit var mChannel: WifiP2pManager.Channel



    private lateinit var mReceiver: BroadcastReceiver
    private lateinit var mIntentFilter: IntentFilter

    private lateinit var btnConnect:Button
    private lateinit var btnDiscover:Button
    private lateinit var btnSend:Button

    private lateinit var recyclerView: RecyclerView
    private lateinit var textStatus: TextView
    lateinit var textWiFiStatus: TextView
    private lateinit var writeMsg: EditText
    lateinit var connectionStatus:TextView

    private val peers = mutableListOf<WifiP2pDevice>()

    private var listAdapter = WiFiPeerListAdapter()
    
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }


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


        //초기에 location은 permission 신청해야 함
        activityResult = registerForActivityResult(ActivityResultContracts.RequestPermission()){}
        activityResult.launch(Manifest.permission.ACCESS_FINE_LOCATION)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            activityResult.launch(Manifest.permission.NEARBY_WIFI_DEVICES)
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
        btnSend.setOnClickListener{}

    }

    override fun onResume() {
        super.onResume()
        mReceiver?.also { receiver ->
            registerReceiver(receiver, mIntentFilter)
        }
    }

    /* unregister the broadcast receiver */
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
        Log.d("test", "제발좀되라")
        
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
        val groupOwnerAddress: String = info.groupOwnerAddress.hostAddress

        // After the group negotiation, we can determine the group owner
        // (server).
        if (info.groupFormed && info.isGroupOwner) {
            // Do whatever tasks are specific to the group owner.
            // One common case is creating a group owner thread and accepting
            // incoming connections.
            connectionStatus.text = "server"

        } else if (info.groupFormed) {
            // The other device acts as the peer (client). In this case,
            // you'll want to create a peer thread that connects
            // to the group owner.
            connectionStatus.text = "client"
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

    private fun onClickSend(){

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

            // If an AdapterView is backed by this data, notify it
            // of the change. For instance, if you have a ListView of
            // available peers, trigger an update.

            listAdapter.listData.clear()
            listAdapter.listData.addAll(peers)

            listAdapter.notifyDataSetChanged()
            Log.d("search result","list Changed")
            Log.d("search result cnt", peers.size.toString())
            // Perform any other updates needed based on the new list of
            // peers connected to the Wi-Fi P2P network.
        }

        if (peers.isEmpty()) {
            Log.d("find peers result", "No devices found")
            return@PeerListListener
        }


    }


}