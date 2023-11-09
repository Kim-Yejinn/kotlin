package com.example.myapplication2

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel

class LoadingViewModel() : ViewModel() {

    private val lodingRepository = LodingRepository()

    val lodingPercent : LiveData<Int>
        get() = lodingRepository._loadingPercent

    fun setLoadingPercent(data:Int){
        lodingRepository.setLoadingPercent(data)
    }

    fun getLoadingPercent() : LiveData<Int>{
        return lodingPercent
    }

}