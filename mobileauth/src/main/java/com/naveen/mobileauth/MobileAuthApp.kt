package com.naveen.mobileauth

import android.annotation.SuppressLint
import android.app.Application
import android.provider.Settings

class MobileAuthApp : Application() {

    companion object {
        private lateinit var mobileAuthApp: MobileAuthApp

        fun getInstance(): MobileAuthApp {
            return mobileAuthApp
        }
    }

    override fun onCreate() {
        super.onCreate()
        mobileAuthApp = this
    }


    @SuppressLint("HardwareIds")
    fun getDeviceId(): String {
        return Settings.Secure.getString(
            applicationContext.contentResolver,
            Settings.Secure.ANDROID_ID
        )
    }
}