package com.example.myapplication2

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class LodingRepository {

    var _loadingPercent = MutableLiveData<Int>()


    fun getLoadingPercent() : LiveData<Int>{
        return _loadingPercent
    }

    fun setLoadingPercent(data:Int){
        // main thread 접근이니까 postValue는 안씀
        // 나중에 다운로드를 서비스로 바꾸면 여기 postValue로 바꿔야 됨
        _loadingPercent.value = data
    }
}