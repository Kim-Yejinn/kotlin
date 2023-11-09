package com.example.myapplication2


import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel

class SocketViewModel():ViewModel() {
    private val socketRepository = SocketRepository()
    val responseData : LiveData<String>
        get() = socketRepository._responseData

    fun setResData(data:String){
        socketRepository.setResponseData(data)
    }

    fun getResData():LiveData<String>{
        return responseData
    }
}