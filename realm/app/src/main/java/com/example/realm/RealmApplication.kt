package com.example.realm

import android.app.Application
import io.realm.kotlin.Realm

class RealmApplication:Application() {
    private var config: RealmApplication? = null

    override fun onCreate() {
        super.onCreate()

    }



}