package com.naveen.mobileauth

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

object FirestoreHelper {

    interface FireStoreInterface {
        fun onSuccess(fcmToken: String)
        fun onFailure(msg: String)
    }

    private const val DEVICE_COLLECTION = "TvDevices"
    private const val USERS_COLLECTION = "Users"
    private const val ACTIVATION_CODE = "activationCode"
    private const val CREATED_DATE = "createdDate"
    private const val ID = "id"
    private const val MAIL = "mail"
    private const val GEN_DATE = "generatedDate"


    fun saveUser(email: String, userId: String) {
        val db = FirebaseFirestore.getInstance()
        val docRef = userId.let { db.collection(USERS_COLLECTION).document(it) }
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val user = hashMapOf(
                        ID to userId,
                        MAIL to email,
                        CREATED_DATE to Date()
                    )
                    docRef.set(user)
                }
            }
            .addOnFailureListener {
                it.printStackTrace()
            }
    }

    fun verifyCode(code: String, callback: FireStoreInterface) {
        val db = FirebaseFirestore.getInstance()
        val docRef = db.collection(DEVICE_COLLECTION).whereEqualTo(ACTIVATION_CODE, code)
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    if (document.documents.isEmpty()) {
                        callback.onFailure("Wrong Activation Code")
                    } else {
                        Log.d("TAG", "verifyCode: ${document.documents.first()}")
                        val generatedTime = document.documents.first().getTimestamp(GEN_DATE)?.toDate()
                        val currentDate = Date()
                        val diffInMillis = currentDate.time - generatedTime?.time!!
                        if (diffInMillis > 3 * 60 * 1000) {
                            callback.onFailure("Activation Code expired, please refresh Tv screen once")
                        } else
                            callback.onSuccess(document.documents.first().id)
                    }
                }
            }
            .addOnFailureListener {
                it.printStackTrace()
                callback.onFailure(it.localizedMessage!!)
            }
    }
}