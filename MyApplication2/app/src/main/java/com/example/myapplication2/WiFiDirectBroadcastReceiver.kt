package com.example.myapplication2

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.wifi.p2p.WifiP2pInfo
import android.net.wifi.p2p.WifiP2pManager
import android.os.Build
import android.provider.Settings
import android.util.Log
class WiFiDirectBroadcastReceiver(
    private val mManager: WifiP2pManager,
    private val mChannel: WifiP2pManager.Channel,
    private val mActivity: MainActivity
) : BroadcastReceiver() {


    @SuppressLint("MissingPermission")
    override fun onReceive(context: Context, intent: Intent) {
        val action: String = intent.action.toString()
        when (action) {
            WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION -> {
                // 와이파이 다이렉트 활성화 여부 확인
                // Check to see if Wi-Fi is enabled and notify appropriate activity
                // Connection state changed! We should probably do something about
                // that.
            }
            WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION -> {
                // 사용가능한 목록이 바뀜을 확인
                // 현재 리스트를 불러오는 것임
                
                // Call WifiP2pManager.requestPeers() to get a list of current peers
                mManager?.requestPeers(mChannel, mActivity.peerListListener)
                Log.d("TAG", "P2P peers changed")
                

            }
            WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION -> {
                // 연결상태가 변경됨 / 연결되었는지 아닌지~~
                mManager?.let { manager ->
//
                    val wifiP2pInfo: WifiP2pInfo? =
                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
                            intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_INFO, WifiP2pInfo::class.java)
                        }else{
                            intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_INFO) as? WifiP2pInfo
                        }
//
                    // 연결 되었을 경우
                    if (wifiP2pInfo != null && wifiP2pInfo.groupFormed) {
                        // 와이파이 다이렉트 연결된 상태
                        mActivity.connectionStatus.text ="Connected"
                        manager.requestConnectionInfo(mChannel, mActivity.connectionListener)
                    }else{
                        // 와이파이 연결 안된 상태
                        mActivity.connectionStatus.text = "DeviceDisconnecteds"
                    }
                }

            }
            WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION -> {
            
                // 디바이스 세부 정보가 변경 - 와이파이 상태라던디
            }
        }
    }
}