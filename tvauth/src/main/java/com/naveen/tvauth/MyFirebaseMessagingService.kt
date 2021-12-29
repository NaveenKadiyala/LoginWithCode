package com.naveen.tvauth

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(p0: RemoteMessage) {
        super.onMessageReceived(p0)
        Log.d("TAG", "onMessageReceived: " + p0.data)
        if (p0.data.containsKey("user_id")) {
            TvAuthApp.getInstance().setFirebaseUserID(p0.data["user_id"]!!)
        }
    }

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
    }
}