package com.naveen.tvauth

import android.os.Build
import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import java.util.*

object FirestoreHelper {

    private const val DEVICE_COLLECTION = "TvDevices"
    private const val USERS_COLLECTION = "Users"
    private const val ACTIVATION_CODE = "activationCode"
    private const val GEN_DATE = "generatedDate"
    private const val DEVICE_NAME = "deviceName"
    private const val USER_ID = "userId"

    private val db by lazy { FirebaseFirestore.getInstance() }

    interface FireStoreHelperInterface {
        fun onSuccess(user: User)
        fun onFailure(msg: String)
    }

    interface LoginListener {
        fun onSuccess(userId: String)
        fun onFailure(msg: String)
    }

    fun saveTvCode(fcmToken: String, activationCode: String, loginListener: LoginListener) {
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
                    val reg = docRef.addSnapshotListener { value, e ->
                        if (e != null) {
                            Log.d("TAG", "Listen failed.", e)
                        }
                        if (value != null && value.exists()) {
                            Log.d("TAG", "Current data: ${value.data}")
                            val userId = value.data!![USER_ID] as String
                            if (userId.isNotEmpty()) {
                                loginListener.onSuccess(userId)
                            }
                        } else {
                            Log.d("TAG", "Current data: null")
                        }
                    }
                }
            }
            .addOnFailureListener {
                it.printStackTrace()
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

    fun getUserDetails(userId: String, callback: FireStoreHelperInterface) {
        val docRef = db.collection(USERS_COLLECTION).document(userId)
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    Log.d("TAG", "verifyCode: $document")
                    val user = document.toObject<User>()
                    user?.let { callback.onSuccess(it) }
                }
            }
            .addOnFailureListener {
                it.printStackTrace()
                callback.onFailure(it.localizedMessage!!)
            }
    }

    class User {
        var createdDate: Timestamp? = null
        var id: String = ""
        var mail: String = ""
    }

}