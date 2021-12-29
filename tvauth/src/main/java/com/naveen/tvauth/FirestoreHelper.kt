package com.naveen.tvauth

import android.os.Build
import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import java.util.*

object FirestoreHelper {

    private const val DEVICE_COLLECTION = "TvDevices"
    private const val USERS_COLLECTION = "Users"
    private const val ACTIVATION_CODE = "activationCode"
    private const val GEN_DATE = "generatedDate"
    private const val DEVICE_NAME = "deviceName"

    private val db by lazy { FirebaseFirestore.getInstance() }

    interface FireStoreHelperInterface {
        fun onSuccess(user: User)
        fun onFailure(msg: String)
    }

    fun saveTvCode(fcmToken: String, activationCode: String) {
        val docRef = fcmToken.let { db.collection(DEVICE_COLLECTION).document(it) }
        docRef.get().addOnSuccessListener { document ->
            if (document != null) {
                val user = hashMapOf(
                    ACTIVATION_CODE to activationCode,
                    GEN_DATE to Date(),
                    DEVICE_NAME to Build.DEVICE
                )
                docRef.set(user)
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