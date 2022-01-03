package com.naveen.tvauth.data

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Build
import android.provider.Settings
import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.naveen.tvauth.TvAuthApp
import java.util.*

object FirestoreHelper {

    private const val DEVICE_COLLECTION = "TvDevices"
    private const val USERS_COLLECTION = "Users"
    private const val ACTIVATION_CODE = "activationCode"
    private const val GEN_DATE = "generatedDate"
    private const val DEVICE_NAME = "deviceName"
    private const val USER_ID = "userId"
    private const val DEVICES = "Devices"
    private const val IS_LOGGED_IN = "loggedIn"
    const val CODE_EXPIRY_IN_MIN = 1L

    private val db by lazy { FirebaseFirestore.getInstance() }

    @SuppressLint("HardwareIds")
    fun saveTvCode(
        activity: Activity,
        activationCode: String,
        callback: (userId: String?, error: String?) -> Unit
    ) {
        val androidId = TvAuthApp.getInstance().getDeviceId()
        val docRef = db.collection(DEVICE_COLLECTION).document(androidId)
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val user = hashMapOf(
                        ACTIVATION_CODE to activationCode,
                        GEN_DATE to Date(),
                        DEVICE_NAME to "${Build.MANUFACTURER} ${Build.MODEL}",
                        USER_ID to ""
                    )
                    docRef.set(user)
                    attachListenerForUserId(docRef, activity, callback)
                }
            }
            .addOnFailureListener {
                it.printStackTrace()
                callback.invoke(null, it.localizedMessage)
            }
    }

    private fun attachListenerForUserId(
        docRef: DocumentReference,
        activity: Activity,
        callback: (userId: String?, error: String?) -> Unit
    ) {
        docRef.addSnapshotListener { value, e ->
            if (e != null) {
                Log.d("TAG", "Listen failed.", e)
            }
            if (value != null && value.exists()) {
                Log.d("TAG", "Current data: ${value.data}")
                val userId = value.data!![USER_ID] as String
                if (userId.isNotEmpty()) {
                    callback.invoke(userId, null)
                }
            } else {
                Log.d("TAG", "Current data: null")
            }
        }
    }

    fun clearUserId(userId: String) {
        val docRef = db.collection(DEVICE_COLLECTION).whereEqualTo(USER_ID, userId)
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null && document.documents.isNotEmpty()) {
                    val docId = document.documents.first().id
                    db.collection(DEVICE_COLLECTION).document(docId)
                        .update(mapOf(USER_ID to ""))
                }
            }
            .addOnFailureListener {
                it.printStackTrace()
            }
    }

    fun addDeviceInUserDevicesList(userId: String) {
        val docRef = db.collection(USERS_COLLECTION).document(userId)
            .collection(DEVICES).document(TvAuthApp.getInstance().getDeviceId())
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    docRef.set(
                        ActiveDevice(
                            isLoggedIn = true
                        )
                    )
                }
            }
            .addOnFailureListener {
                it.printStackTrace()
            }
    }

    fun clearIsLoggedIn(userId: String) {
        val docRef = db.collection(USERS_COLLECTION).document(userId)
            .collection(DEVICES).document(TvAuthApp.getInstance().getDeviceId())
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    docRef.update(mapOf(IS_LOGGED_IN to false))
                }
            }
            .addOnFailureListener {
                it.printStackTrace()
            }
    }

    fun getUserDetails(userId: String, callback: (user: User?, error: String?) -> Unit) {
        val docRef = db.collection(USERS_COLLECTION).document(userId)
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    Log.d("TAG", "verifyCode: $document")
                    val user = document.toObject<User>()
                    user?.let { callback.invoke(it, null) }
                }
            }
            .addOnFailureListener {
                it.printStackTrace()
                callback.invoke(null, it.localizedMessage!!)
            }
    }

    class User {
        var createdDate: Timestamp? = null
        var id: String = ""
        var mail: String = ""
    }

    data class ActiveDevice(
        val deviceName: String = "${Build.MANUFACTURER} ${Build.MODEL}",
        val loggedInTime: Date = Date(),
        val isLoggedIn: Boolean = false
    )

}