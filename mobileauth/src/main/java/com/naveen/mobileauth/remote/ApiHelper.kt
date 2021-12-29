package com.naveen.mobileauth.remote

import android.util.Log
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.naveen.mobileauth.BuildConfig
import org.json.JSONException
import org.json.JSONObject

object ApiHelper {

    interface ApiResponseListener {
        fun onSuccess()
        fun onFailure(msg: String)
    }

    fun sendPushNotification(
        fcmToken: String,
        userId: String,
        callback: ApiResponseListener
    ) {
        val jsonObject = JSONObject()
        try {
            val dataObject = JSONObject()
            dataObject.put("user_id", userId)
            jsonObject.put("to", fcmToken)
            jsonObject.put("data", dataObject)
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        AndroidNetworking.post(BuildConfig.API_URL + "fcm/send")
            .addHeaders("Authorization", "key=${BuildConfig.FIREBASE_SERVER_KEY}")
            .addJSONObjectBody(jsonObject)
            .setTag("Test")
            .setPriority(Priority.HIGH)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject) {
                    Log.d("TAG", "onResponse: $response")
                    if (response.has("success") && response["success"] == 1) {
                        callback.onSuccess()
                    }
                }

                override fun onError(error: ANError) {
                    Log.d("TAG", "onError errorBody : " + error.errorBody)
                    error.printStackTrace()
                    callback.onFailure(error.errorDetail)
                }
            })
    }
}