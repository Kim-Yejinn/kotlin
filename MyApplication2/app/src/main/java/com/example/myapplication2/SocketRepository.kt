package com.example.myapplication2

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class SocketRepository {

    var _responseData = MutableLiveData<String>()


    fun getResponseData(): LiveData<String> {
        return _responseData
    }

    fun setResponseData(data: String) {
        _responseData.postValue(data)
    }

}