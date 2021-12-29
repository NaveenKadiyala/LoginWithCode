package com.naveen.tvauth

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class TvAuthApp : Application() {

    private var firebaseUserId = MutableLiveData<String>()
    var _firebaseUserId: LiveData<String> = firebaseUserId

    fun setFirebaseUserID(userId: String) {
        Log.d("TAG", "setFirebaseUserID: $userId")
        firebaseUserId.postValue(userId)
    }

    companion object {
        private lateinit var tvAuthApp: TvAuthApp

        fun getInstance(): TvAuthApp {
            return tvAuthApp
        }
    }

    override fun onCreate() {
        super.onCreate()
        tvAuthApp = this
    }
}