package com.example.myapplication2

import android.app.Application
import androidx.lifecycle.ViewModelProvider

class App : Application() {
    val socketViewModel by lazy {
        ViewModelProvider.AndroidViewModelFactory.getInstance(this).create(SocketViewModel::class.java)
    }
    init {
        INSTANCE = this
    }

    override fun onCreate() {
        super.onCreate()
    }

    companion object{
        lateinit var INSTANCE: App

    }

}