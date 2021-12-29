package com.naveen.tvauth.data

import android.app.Activity
import android.os.Build
import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import java.util.*

object FirestoreHelper {

    private const val DEVICE_COLLECTION = "TvDevices"
    private const val USERS_COLLECTION = "Users"
    private const val ACTIVATION_CODE = "activationCode"
    private const val GEN_DATE = "generatedDate"
    private const val DEVICE_NAME = "deviceName"
    private const val USER_ID = "userId"

    private val db by lazy { FirebaseFirestore.getInstance() }

    fun saveTvCode(
        activity: Activity,
        fcmToken: String,
        activationCode: String,
        callback: (userId: String?, error: String?) -> Unit
    ) {
        val docRef = fcmToken.let { db.collection(DEVICE_COLLECTION).document(it) }
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val user = hashMapOf(
                        ACTIVATION_CODE to activationCode,
                        GEN_DATE to Date(),
                        DEVICE_NAME to Build.DEVICE,
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
        docRef.addSnapshotListener(activity) { value, e ->
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
                if (document != null) {
                    val docId = document.documents.first().id
                    db.collection(DEVICE_COLLECTION).document(docId)
                        .update(mapOf(USER_ID to ""))
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

}