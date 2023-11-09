package com.example.myapplication2

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.wifi.aware.WifiAwareManager

// 와이파이 연결상태 확인
class WifiAwareBroadcastReceiver (
    private val aManager: WifiAwareManager
):BroadcastReceiver(){
    override fun onReceive(context: Context?, intent: Intent?) {
        // discard current sessions
        if (aManager.isAvailable) {

        } else {

        }
    }
}