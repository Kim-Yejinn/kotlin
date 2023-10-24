package com.example.wifidirect2

import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter
import android.net.wifi.p2p.WifiP2pManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

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
        //exqListener()




    }

    fun onClickDiscover(v: View) {

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
            wifiManager.isWifiEnabled = false
            btnOnOff.setText("ON")

        }else{
            wifiManager.isWifiEnabled = true
            btnOnOff.setText("OFF")
        }

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