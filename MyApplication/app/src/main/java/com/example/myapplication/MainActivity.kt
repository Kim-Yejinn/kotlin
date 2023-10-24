package com.example.myapplication

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.wifi.WifiManager
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat

class MainActivity : AppCompatActivity() {
    private lateinit var btnOnOff: Button
    private lateinit var btnDiscover: Button
    private lateinit var btnSend: Button
    private lateinit var listView: ListView
    private lateinit var read_msg_box: TextView
    private lateinit var connectionStatus: TextView
    private lateinit var writeMsg: EditText
    private lateinit var wifiManager: WifiManager
    private lateinit var mManager: WifiP2pManager
    private lateinit var mChannel: WifiP2pManager.Channel
    private lateinit var mReciver: BroadcastReceiver
    private lateinit var mIntenFilter: IntentFilter
    private  var peers = ArrayList<WifiP2pDevice>()
    private  var deviceNameArray= ArrayList<String>()
    private  var deviceArray= ArrayList<WifiP2pDevice>()
    var ingredientsList =  ArrayList<Ingredient>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initialWork()
        permission()
    }
    private val permissionsToRequest = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.NEARBY_WIFI_DEVICES,
        Manifest.permission.ACCESS_WIFI_STATE,
        Manifest.permission.CHANGE_WIFI_STATE,
        Manifest.permission.CHANGE_NETWORK_STATE,
        Manifest.permission.INTERNET
    )
    // 권한 요청을 처리하는 런처를 등록
    private var requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
        if (isGranted) {
            // 현재 권한이 허용되었을 때, 다음 권한 요청을 수행
            currentPermissionIndex++
            requestNextPermission()
        } else {
            // 권한이 거부되었을 때 작업 수행 (예: 다이얼로그 표시, 앱 종료 등)
            requestNextPermission()
//            showPermissionDeniedDialog()
        }
    }

    private var currentPermissionIndex = 0
    private fun requestNextPermission() {
        if (currentPermissionIndex < permissionsToRequest.size) {
            val permissionToRequest = permissionsToRequest[currentPermissionIndex]
            requestPermissionLauncher.launch(permissionToRequest)
        } else {
            // 모든 권한이 허용된 경우 다음 작업 수행
            // 예: Wi-Fi Direct 동작 시작
        }
    }
    private fun permission() {
        // 초기 권한 요청 시작
        requestNextPermission()
    }
    private fun showPermissionDeniedDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Permission Denied")
            .setMessage("This app requires certain permissions to function properly. Please grant the required permissions in the app settings.")
            .setPositiveButton("OK") { _, _ ->
                // 사용자가 확인 버튼을 누르면 앱을 종료할 수도 있습니다.
                finish()
            }
            .setCancelable(false) // 대화 상자 바깥 부분을 터치해도 닫히지 않도록 설정
            .show()
    }

    fun onClickDiscover(v: View) {
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
                connectionStatus.text = ("Discovery Started")
            }

            override fun onFailure(reason: Int) {
                connectionStatus.text = ("Discovery not Started")
            }
        })
    }
    fun onClickONOff(v:View){

        if(wifiManager.isWifiEnabled){
//            wifiManager.isWifiEnabled = false
            showWiFiSettingPanel()
            btnOnOff.setText("ON")

        }else{
//            wifiManager.isWifiEnabled = true
            btnOnOff.setText("OFF")
        }

    }
    @RequiresApi(Build.VERSION_CODES.Q)
    fun showWiFiSettingPanel() {
        val panelIntent = Intent(Settings.Panel.ACTION_WIFI)
        this.startActivity(panelIntent)
    }


    private fun initialWork() {
        btnOnOff=findViewById(R.id.onOff)
        btnDiscover=findViewById(R.id.discover)
        btnSend=findViewById(R.id.sendButton)
        listView=findViewById(R.id.peerListView)
        read_msg_box=findViewById(R.id.readMsg)
        connectionStatus=findViewById(R.id.connectionStatus)
        writeMsg=findViewById(R.id.writeMsg)
        wifiManager= applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

        mManager= getSystemService(Context.WIFI_P2P_SERVICE) as WifiP2pManager
        mChannel=mManager.initialize(this, mainLooper, null)

        mReciver=  WiFiDirectBroadcastReceiver(mManager, mChannel, this )
        mIntenFilter = IntentFilter()
        mIntenFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION)
        mIntenFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION)
        mIntenFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION)
        mIntenFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION)



    }

    val peerListListener = WifiP2pManager.PeerListListener { peerList ->
        val refreshedPeers = peerList.deviceList
        if (refreshedPeers != peers) {
            peers.clear()
            peers.addAll(refreshedPeers)
            ingredientsList.clear()
            for (d1 in peers) {
                if (d1.deviceName.contains("°")) {
                    Log.d("TEST", "IF1")
                    val name = d1.deviceName.split("°").toTypedArray()
                    ingredientsList.add(Ingredient(name[0], d1.deviceAddress, "5"))
                }
                if (d1.deviceName.contains("|")) {
                    Log.d("TEST", "IF2")
                    val name = d1.deviceName.split("|").toTypedArray()
                    ingredientsList.add(Ingredient(name[0] + " (Conectado)", d1.deviceAddress, "5"))
                }
            }
        }
        if (peers.size == 0) {
            Log.d("MainActivity", "No peers found")
        } else {
            Log.d("MainActivity", "PE")
        }
    }


    override fun onResume() {
        super.onResume()
        registerReceiver(mReciver,mIntenFilter)
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(mReciver)
    }
}