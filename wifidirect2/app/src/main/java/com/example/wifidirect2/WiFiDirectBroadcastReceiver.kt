package com.example.wifidirect2

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pManager
import android.util.Log
import android.widget.Toast
import java.nio.channels.Channel

class WiFiDirectBroadcastReceiver(
    private var mManager: WifiP2pManager,
    private var mChannel: WifiP2pManager.Channel,
    private var mActivity: MainActivity
) : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        var action = intent.action

        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION == action){
            var state  =intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE,-1)

            if (state==WifiP2pManager.WIFI_P2P_STATE_ENABLED){
                Toast.makeText(context,"Wifi is on",Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(context,"Wifi is off",Toast.LENGTH_SHORT).show()
            }

        }else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION == action){


            mManager.requestPeers(mChannel,mActivity.peerListListener)

        }else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION == action){

        }else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION == action){

        }


    }
}