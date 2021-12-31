package com.naveen.tvauth

import android.annotation.SuppressLint
import android.app.Application
import android.provider.Settings

class TvAuthApp : Application() {

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


    @SuppressLint("HardwareIds")
    fun getDeviceId(): String {
        return Settings.Secure.getString(
            applicationContext.contentResolver,
            Settings.Secure.ANDROID_ID
        )
    }
}